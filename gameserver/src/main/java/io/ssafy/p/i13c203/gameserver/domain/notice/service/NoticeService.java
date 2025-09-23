package io.ssafy.p.i13c203.gameserver.domain.notice.service;

import io.ssafy.p.i13c203.gameserver.domain.member.entity.Member;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import io.ssafy.p.i13c203.gameserver.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    public Notice createNotice(Member member, String title, String content, NoticeType type) {


        Notice notice = Notice.builder()
                .member(member)
                .title(title)
                .content(content)
                .type(type)
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        log.info("공지사항 생성 완료: id={}, title={}", savedNotice.getId(), savedNotice.getTitle());

        return savedNotice;
    }

    @Transactional(readOnly = true)
    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Notice> getNoticesByType(NoticeType type) {
        return noticeRepository.findByTypeOrderByCreatedAtDesc(type);
    }


    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new IllegalArgumentException("Notice not found");
        }
        noticeRepository.deleteById(id);
        log.info("공지사항 삭제 완료: id={}", id);
    }
}