package io.ssafy.p.i13c203.gameserver.domain.image.repository;

import io.ssafy.p.i13c203.gameserver.domain.image.entity.SuggestionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionImageRepository extends JpaRepository<SuggestionImage, Long> {
}