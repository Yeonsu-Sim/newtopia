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

    boolean delete(String key) throws IOException;
    boolean exists(String key) throws IOException;
}
