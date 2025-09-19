package io.ssafy.p.i13c203.gameserver.seed;

import io.ssafy.p.i13c203.gameserver.infra.storage.FileStorage;
import io.ssafy.p.i13c203.gameserver.global.utils.StorageKeyBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractImageSeeder<T> {

    private FileStorage storage;

    protected AbstractImageSeeder(FileStorage storage) {
        this.storage = storage;
    }

    /** 그룹명: "npc" | "ending" */
    protected abstract String groupName();

    /** 설정 주입 */
    protected abstract SeedProps props();

    /** 업로드 대상 엔티티 로드 (코드/ID 목록 알려줄테니 필터해도 되고 전체 로드도 OK) */
    protected abstract List<T> loadTargets(Set<String> possibleKeys);

    /** 매칭 키 추출: 엔티티에서 code/id를 꺼내 파일명 매칭에 씀 */
    protected abstract String keyOf(T entity, String matchBy);

    /** 이미 링크 되었는가? (멱등 방지용) */
    protected abstract boolean alreadyLinked(T entity);

    /** 링크(업데이트): storageKey, publicUrl 저장 */
    protected abstract void link(T entity, UploadMeta meta);

    /** 배치 저장 */
    protected abstract void saveAll(List<T> batch);

    @Transactional
    public void runOnce() {
        var p = props();
        if (!p.enabled()) {
            log.info("[{} seed] disabled. skip.", groupName());
            return;
        }
        try {
            // 1) 리소스에서 이미지 파일 읽기
            var resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(p.location());

            // 2) 파일명(확장자 제외) -> Resource 매핑
            Map<String, Resource> keyToRes = Arrays.stream(resources).collect(Collectors.toMap(
                    r -> {
                        String fn = Objects.requireNonNull(r.getFilename());
                        int dot = fn.lastIndexOf('.');
                        return (dot > 0) ? fn.substring(0, dot) : fn;
                    },
                    r -> r,
                    (a, b) -> a
            ));

            // 3) 대상 엔티티 로드(성능 걱정되면 findByCodeIn/IdIn으로 좁혀도 됨)
            List<T> targets = loadTargets(keyToRes.keySet());

            int success = 0, skip = 0, fail = 0;
            List<T> dirty = new ArrayList<>();

            for (T e : targets) {
                try {
                    if (alreadyLinked(e)) { // 멱등: 이미 링크된 건 건너뜀
                        skip++;
                        continue;
                    }
                    String matchKey = keyOf(e, p.matchBy()); // code 또는 id 문자열
                    Resource imgRes = keyToRes.get(matchKey);
                    if (imgRes == null) { // 매칭 이미지 없으면 건너뜀(로그만)
                        log.warn("[{} seed] image not found for {}", groupName(), matchKey);
                        skip++;
                        continue;
                    }

                    // 4) codeKey 생성 (공개 도메인 사용)
                    String filename = Objects.requireNonNull(imgRes.getFilename());
                    String ext = StorageKeyBuilder.extOf(filename, "png");
                    String codeKey = StorageKeyBuilder.buildPublicByCode(p.domain(), keyOf(e, p.matchBy()), ext);

                    boolean exists = storage.exists(codeKey);
                    String mode = p.overwrite(); // never | if_changed | always

                    if (!exists) {
                        // 업로드
                        long size = safeContentLength(imgRes);
                        String ct  = contentType(ext);
                        try (InputStream in = imgRes.getInputStream()) { storage.store(codeKey, in, size, ct); }
                        String url = storage.publicUrl(codeKey);
                        log.info("[{} seed] uploaded new object. key={}, size={}, ct={}", groupName(), codeKey, size, ct);
                        link(e, meta(codeKey, url, ct, size, filename));
                    } else {
                        switch (mode) {
                            case "always" -> {
                                long size = safeContentLength(imgRes);
                                String ct  = contentType(ext);
                                try (InputStream in = imgRes.getInputStream()) {
                                    // 덮어쓰기(DELETE 금지; 원샷 교체)
                                    storage.store(codeKey, in, size, ct);
                                }
                                String url = storage.publicUrl(codeKey);
                                log.info("[{} seed] overwrote existing object (always). key={}, size={}, ct={}", groupName(), codeKey, size, ct);
                                link(e, meta(codeKey, url, ct, size, filename));
                            }
                            case "if_changed" -> {
                                // 로컬 해시 계산
                                String localMd5 = DigestUtils.md5Hex(imgRes.getInputStream());
                                var st = storage.stat(codeKey);

                                boolean same = localMd5.equalsIgnoreCase(st.etag());
                                if (same) {
                                    String url = storage.publicUrl(codeKey);
                                    log.info("[{} seed] object unchanged; DB restore only. key={}, size={}, ct={}", groupName(), codeKey, st.sizeBytes(), st.contentType());
                                    link(e, meta(codeKey, url, st.contentType(), st.sizeBytes(), filename));
                                } else {
                                    long size = safeContentLength(imgRes);
                                    String ct  = contentType(ext);
                                    try (InputStream in = imgRes.getInputStream()) { storage.store(codeKey, in, size, ct); }
                                    String url = storage.publicUrl(codeKey);
                                    log.info("[{} seed] object changed; overwrite. key={}, oldEtag={}, size={}, ct={}", groupName(), codeKey, st.etag(), size, ct);
                                    link(e, meta(codeKey, url, ct, size, filename));
                                }
                            }
                            default -> {  // "never"
                                var st = storage.stat(codeKey);
                                String url = storage.publicUrl(codeKey);
                                log.info("[{} seed] found existing; DB restore only. key={}, size={}, ct={}", groupName(), codeKey, st.sizeBytes(), st.contentType());
                                link(e, meta(codeKey, url, st.contentType(), st.sizeBytes(), filename));
                            }
                        }
                    }

                    // 엔티티에 반영
                    dirty.add(e);
                    success++;
                } catch (Exception ex) {
                    log.error("[{} seed] fail entity={}, ex={}", groupName(), e, ex.toString());
                    fail++;
                }
            }

            // 8) 배치 저장
            if (!dirty.isEmpty()) saveAll(dirty);

            log.info("[{} seed] done. success={}, skip={}, fail={}", groupName(), success, skip, fail);
        } catch (Exception e) {
            log.error("[{} seed] fatal", groupName(), e);
        }
    }

    private long safeContentLength(Resource r) {
        try {
            long len = r.contentLength();
            return Math.max(0L, len);
        } catch (Exception e) {
            return 0L;
        }
    }

    private String contentType(String ext) {
        if (ext == null) return "application/octet-stream";
        return switch (ext.toLowerCase()) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private UploadMeta meta(String key, String url, String ct, long size, String original) {
        return new UploadMeta(
                java.util.UUID.nameUUIDFromBytes(key.getBytes()),
                key, url, ct, size, original
        );
    }

    private boolean roughlySame(FileStorage.FileStat st, String localSha256) {
        // MinIO/S3의 ETag는 단일 파트 업로드면 MD5.
        // 완벽 비교를 원하면 객체 메타에 sha256을 저장/읽는 방법으로 운영하세요.
        return false; // 최소 구현에서는 false로 두고 필요시 확장
    }


    /** 간단한 레코드 형태의 설정 DTO */
    public record SeedProps(boolean enabled, String overwrite, String location, String matchBy, String domain) {}

    protected static record UploadMeta(
            java.util.UUID publicId,
            String storageKey,
            String url,
            String contentType,
            long sizeBytes,
            String originalName
    ) {}
}
