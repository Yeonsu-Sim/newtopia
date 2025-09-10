package io.ssafy.p.i13c203.gameserver.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class APIError<T> {
    private String code;      // 예: "USER", "GAME" 등
    private T details;   // 상세 설명 (null 가능)
}