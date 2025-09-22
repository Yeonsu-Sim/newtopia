package io.ssafy.p.i13c203.gameserver.domain.gameresult.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BlockType {
    BULLETS,
    RICH_TEXT,
    TEXT

//    BULLETS("bullets"),
//    RICH_TEXT("richText"),
//    TEXT("text");
//
//    @JsonValue
//    private final String json;  // 직렬화 시 이 문자열로 나감
//    BlockType(String json) { this.json = json; }
//
//    public String getJson() { return json; }
}