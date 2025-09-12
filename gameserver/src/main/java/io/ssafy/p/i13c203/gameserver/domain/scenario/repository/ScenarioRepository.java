package io.ssafy.p.i13c203.gameserver.domain.scenario.repository;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {}