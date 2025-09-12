package io.ssafy.p.i13c203.gameserver.domain.ending.doc;

public record EndingDoc(
        String code,
        String title,
        String content,
        String condition,    // "economy==100" 등 (표시/로그용)
        String endingS3Key    // 이미지 키(없으면 null)
) {}