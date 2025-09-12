package io.ssafy.p.i13c203.gameserver.domain.game.repository;

import io.ssafy.p.i13c203.gameserver.domain.game.redis.IdemRecord;
import org.springframework.data.repository.CrudRepository;

public interface IdemRecordRepository extends CrudRepository<IdemRecord, String> {}