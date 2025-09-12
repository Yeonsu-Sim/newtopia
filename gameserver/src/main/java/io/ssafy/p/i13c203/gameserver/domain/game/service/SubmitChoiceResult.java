package io.ssafy.p.i13c203.gameserver.domain.game.service;

import io.ssafy.p.i13c203.gameserver.domain.game.doc.CardDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.CountryStatsDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.doc.HistoryEntryDoc;
import io.ssafy.p.i13c203.gameserver.domain.game.entity.Game;

public record SubmitChoiceResult(Applied applied, NextTurn nextTurn) {
    public static SubmitChoiceResult from(Game game, HistoryEntryDoc applied, CardDoc next) {
        return new SubmitChoiceResult(
                new Applied(applied.turn(), applied.choosedCode(), applied.countryStats()),
                new NextTurn(game.getTurn(), new CountryStatsDoc(
                        game.getCountryStats().getEconomy(),
                        game.getCountryStats().getDefense(),
                        game.getCountryStats().getPublicSentiment(),
                        game.getCountryStats().getEnvironment()
                ), next)
        );
    }
    public record Applied(int turn, String choosedCode, CountryStatsDoc countryStats) {}
    public record NextTurn(int turn, CountryStatsDoc countryStats, CardDoc card) {}
}
