package io.ssafy.p.i13c203.gameserver.domain.scenario.service;

import io.ssafy.p.i13c203.gameserver.domain.scenario.dto.NpcResponse;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NpcRepository;
import io.ssafy.p.i13c203.gameserver.global.exception.ErrorCode;
import io.ssafy.p.i13c203.gameserver.global.exception.NotFoundException;
import io.ssafy.p.i13c203.gameserver.global.utils.NpcPicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NpcService {

    private final NpcRepository npcRepository;

    // category를 기준으로 Npc 찾기
    //
    public Npc getNpcByCategory(String category){

        return null;
    }


    public Npc getNpcByCategoryPlainJava(String category){

        long npcId = (long)NpcPicker.pick(category);



        return npcRepository.findById(npcId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
    }



}
