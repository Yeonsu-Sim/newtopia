package io.ssafy.p.i13c203.gameserver.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class APIResponse<T, K> {
    @Schema(example = "success | error")
    private String status;   // "success" or "error"
    private T data;
    @Schema(example = "응답에 관한 메세지")
    private String message;
    private APIError<K> error;

    // 성공 응답: 데이터만
    public static <T, K> APIResponse<T, K> success(T data) {
        return new APIResponse<>("success", data, "요청에 성공했습니다.", null);
    }

    // 성공 응답: 메시지 + 데이터
    public static <T, K> APIResponse<T, K> success(String message, T data) {
        return new APIResponse<>("success", data, message, null);
    }

    // 실패 응답: 에러 코드와 메시지
    public static <T, K> APIResponse<T, K> fail(String code, String message) {
        return new APIResponse<>("error", null, message, new APIError<>(code, null));
    }


    // 실패 응답: 에러 코드 + 메시지 + 상세 설명
    public static <T, K> APIResponse<T, K> fail(String code, String message, K details) {
        return new APIResponse<>("error", null, message, new APIError<K>(code, details));
    }

    // 실패 응답 : 에러코드 + 데이터 + 메시지 + 상세설명
    public static <T, K> APIResponse<T, K> fail(String code, String message, T data, K details) {
        return new APIResponse<>("error", data, message, new APIError<K>(code, details));
    }
}
