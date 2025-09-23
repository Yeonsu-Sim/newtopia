package io.ssafy.p.i13c203.gameserver.domain.suggestion.repository;

import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.Suggestion;
import io.ssafy.p.i13c203.gameserver.domain.suggestion.entity.SuggestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    List<Suggestion> findByCategory(SuggestionCategory category);
}
