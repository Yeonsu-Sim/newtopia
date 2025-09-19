package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Part {
    CONTEXT("context"),
    SUMMARY("summary"),
    GRAPH("graph");

    private final String value;

    @JsonValue
    public String json(){ return value; }

    @JsonCreator
    public static Part from(String v){
        for (var p: values()) if (p.value.equalsIgnoreCase(v)) return p;
        throw new IllegalArgumentException("Invalid part: " + v);
    }
}

