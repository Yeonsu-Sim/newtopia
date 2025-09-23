package io.ssafy.p.i13c203.gameserver.domain.notice.repository;

import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByTypeOrderByCreatedAtDesc(NoticeType type);

    List<Notice> findAllByOrderByCreatedAtDesc();

    Page<Notice> findByTypeOrderByCreatedAtDesc(NoticeType type, Pageable pageable);
}