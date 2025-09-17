package io.ssafy.p.i13c203.gameserver.common.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortDirection {

    ASC("asc"),
    DESC("desc");

    private final String value;

    @JsonValue
    public String json(){ return value; }

    @JsonCreator
    public static SortDirection from(String v){
        for (var s: values()) if (s.value.equalsIgnoreCase(v)) return s;
        throw new IllegalArgumentException("Invalid sort: " + v);
    }
}
