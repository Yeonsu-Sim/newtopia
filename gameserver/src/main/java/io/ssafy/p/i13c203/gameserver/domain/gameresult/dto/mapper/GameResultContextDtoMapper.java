package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.mapper;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.ContextDto;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.entity.GameResult;
import org.springframework.stereotype.Component;

@Component
public class GameResultContextDtoMapper {

    public ContextDto toContext(GameResult gr) {
        var reportContext = gr.getContext();
        var finalCountryStats = reportContext.countryStats();

        return new ContextDto(
                reportContext.countryName(),
                reportContext.finalTurnNumber(),
                reportContext.generatedAt(),
                new ContextDto.CountryStats(
                        finalCountryStats.economy(),
                        finalCountryStats.defense(),
                        finalCountryStats.publicSentiment(),
                        finalCountryStats.environment()
                )
        );
    }

}
