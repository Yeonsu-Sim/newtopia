package io.ssafy.p.i13c203.gameserver.domain.scenario.repository;

import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcRepository extends JpaRepository<Npc, Long> {}
