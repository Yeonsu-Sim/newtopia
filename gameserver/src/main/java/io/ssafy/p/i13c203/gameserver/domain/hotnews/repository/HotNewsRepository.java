package io.ssafy.p.i13c203.gameserver.domain.hotnews.repository;

import io.ssafy.p.i13c203.gameserver.domain.hotnews.entity.HotNews;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotNewsRepository extends JpaRepository<HotNews,Long> {
  List<HotNews> findByOrderByPublishedAtDesc(Pageable pageable);
}
