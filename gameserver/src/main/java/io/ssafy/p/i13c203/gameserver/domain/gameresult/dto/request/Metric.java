package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Metric {
    ECONOMY("economy"),
    DEFENSE("defense"),
    ENVIRONMENT("environment"),
    PUBLIC_SENTIMENT("publicSentiment");

    private final String value;

    @JsonValue
    public String json(){ return value; }

    @JsonCreator
    public static Metric from(String v){
        for (var m: values()) if (m.value.equalsIgnoreCase(v)) return m;
        throw new IllegalArgumentException("Invalid metric: " + v);
    }
}
