package io.ssafy.p.i13c203.gameserver.infra.storage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


// @Service

//@RequiredArgsConstructor
//@Slf4j
//public class LocalFileStorage implements FileStorage{
//
//    @Value("${app.storage.local.base-dir}")
//    private  String baseDir;
//
//    @Value("${app.storage.local.public-base-url}")
//    private String base;
//
//    @Override
//    public void store(String key, InputStream input, long size, String contentType) throws IOException {
//        Path target = resolveKey(key);
//        Files.createDirectories(target.getParent());
//        Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
//    }
//
//    @Override
//    public String publicUrl(String key) {
//        return base.endsWith("/")? base + key : base + "/" + key;
//    }
//
//    @Override
//    public boolean delete(String key) throws IOException {
//        Path path = resolveKey(key);
//        return Files.deleteIfExists(path);
//
//    }
//
//    @Override
//    public boolean exists(String key) throws IOException {
//        Path path = resolveKey(key);
//
//        return Files.exists(path);
//    }
//
//    private Path resolveKey(String key) throws IOException {
//        // 경로 탈출 방지
//        Path base = Paths.get(baseDir).toAbsolutePath().normalize();
//
//        Path target = base.resolve(key).normalize();
//        log.info("base : {}, target : {}", base, target.toString());
//
//        if (!target.startsWith(base)) {
//            throw new IOException("Invalid storage key (path traversal)");
//        }
//        return target;
//    }
//}
