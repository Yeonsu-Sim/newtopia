package io.ssafy.p.i13c203.gameserver.domain.game.dto;

public record CountryStatsDeltaDto(int economy, int defense, int publicSentiment, int environment) {
    public static CountryStatsDeltaDto of(int eco, int def, int opi, int env) {
        return new CountryStatsDeltaDto(eco, def, opi, env);
    }
}
