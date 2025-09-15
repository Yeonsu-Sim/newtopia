package io.ssafy.p.i13c203.gameserver.global.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StorageKeyBuilder {
    private StorageKeyBuilder(){}

    public static String build(Long memberId, String domain, UUID fileId, String ext){
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("u/%d/%s/%s/%s.%s", memberId, domain, date, fileId, ext.toLowerCase());
    }

    public static String buildPublic(String domain, UUID fileId, String ext){
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("public/%s/%s/%s.%s", domain, date, fileId, ext.toLowerCase());
    }

    public static String extOf(String originalName, String contentType) {
        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf('.') + 1);
        }
        // fallback 간단 매핑
        if ("image/png".equals(contentType)) return "png";
        if ("image/jpeg".equals(contentType)) return "jpg";
        if ("image/gif".equals(contentType)) return "gif";
        if ("image/webp".equals(contentType)) return "webp";
        return "bin";
    }
}
