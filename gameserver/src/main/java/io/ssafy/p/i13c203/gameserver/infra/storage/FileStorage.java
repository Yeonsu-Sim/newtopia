package io.ssafy.p.i13c203.gameserver.infra.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileStorage {
    void store(String key, InputStream input, long size, String contentType) throws IOException;

    default void store(String key, MultipartFile file) throws IOException {
        try(InputStream in = file.getInputStream()){
            store(key, in, file.getSize(), file.getContentType());
        }
    }

    String publicUrl(String key);

    FileStat stat(String key) throws IOException;

    boolean delete(String key) throws IOException;
    boolean exists(String key) throws IOException;

    /** 파일 크기/콘텐츠타입 등을 담기 위한 레코드 */
    record FileStat(long sizeBytes, String contentType, String etag) {}
}
