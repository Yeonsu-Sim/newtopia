package io.ssafy.p.i13c203.gameserver.domain.suggestion.service;

import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.SuggestionImage;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.ImageRepository;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.SuggestionImageRepository;
import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
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

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final SuggestionImageRepository suggestionImageRepository;


    @Transactional
    public SuggestionCreateResponse createSuggestion(SuggestionCreateRequest request, Member member) {

        Suggestion suggestion = Suggestion.builder()
                .text(request.getText())
                .title(request.getTitle())
                .member(member)
                .category(request.getSuggestionCategory())
                .build();

        List<Long> imageIds = IntStream.of(request.getImageIds())
                .asLongStream()
                .boxed()
                .toList();

        // 1. suggestion 영속화
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);

        // 2. suggestion <-> image 관계 추가
        if (!imageIds.isEmpty()) {
            List<Image> images = imageRepository.findAllById(imageIds);
            
            // TEMP 상태 이미지들을 ATTACHED로 변경하고 SuggestionImage 생성
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                
                // 이미지 상태를 ATTACHED로 변경
                image.setStatus(ImageStatus.ATTACHED);
                
                // SuggestionImage 연관관계 생성
                SuggestionImage suggestionImage = SuggestionImage.builder()
                        .suggestion(savedSuggestion)
                        .image(image)
                        .sortOrder(i)
                        .build();
                
                suggestionImageRepository.save(suggestionImage);
            }
        }

        return SuggestionCreateResponse.builder()
                .suggestionId(savedSuggestion.getId())
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
