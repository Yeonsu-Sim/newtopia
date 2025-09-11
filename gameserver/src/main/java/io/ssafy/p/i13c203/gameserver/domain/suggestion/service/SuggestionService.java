package io.ssafy.p.i13c203.gameserver.domain.suggestion.service;

import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.ImageRepository;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Role;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionCreateRequest;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.request.SuggestionRequest;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionCreateResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.dto.response.SuggestionResponse;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.Suggestion;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.repository.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;


    // TODO  image TEMP -> ATTACH 필요
    // 1. create Suggestion
    // 2. suggestion <-> image 들 관계 테이블에 추가
    //  image 올린 사람이랑 suggestion 한사람 같은지 확인
    //
    @Transactional
    public SuggestionCreateResponse createSuggestion(SuggestionCreateRequest request) {

        Suggestion suggestion = Suggestion.builder()
                .text(request.getText())
                .title(request.getTitle())
                .member(memberRepository.findById(request.getMemberId()).orElseThrow())
                .category(request.getSuggestionCategory())
                .build();

        List<Long> imageIds = IntStream.of(request.getImageIds())
                .asLongStream()
                .boxed()
                .toList();

        //1. suggestion 영속화
        Suggestion save = suggestionRepository.save(suggestion);

        //2. suggestion, image 관계 추가
        // imageIds에는 이미지가 0 ~ 몇개가 될지 모름
        // 이때 imageRepository에서 image들을 꺼내와서 작업해야함
        List<Image> allById = imageRepository.findAllById(imageIds);

        for (Image image : allById) {
            image.setSuggestion(save);
        }


        return SuggestionCreateResponse.builder()
                .suggestionId(save.getId())
                .build();
    }

    // TODO 페이지네이션, 건의사항에 답변완료 같은 플래그를 넣어서
    // 이미 본 것은 안보여지게 하기.


    public List<SuggestionResponse> getSuggestion(SuggestionRequest request) {
        return getSuggestionV1(request.getMemberId());
    }

    private List<SuggestionResponse> getSuggestionV1(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow();

//        if (member.getRole() != Role.ADMIN) {
//            throw new RuntimeException("관리자가 아닙니다.");
//        }


        List<Suggestion> all = suggestionRepository.findAll();


        return all.stream()
                .map(s -> SuggestionResponse.builder()
                        .title(s.getTitle())
                        .text(s.getText())
                        .category(s.getCategory())
                        .createdAt(s.getCreatedAt())

                        .build())
                .toList();
    }

}
