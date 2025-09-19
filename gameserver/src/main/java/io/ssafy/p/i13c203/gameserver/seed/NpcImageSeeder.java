package io.ssafy.p.i13c203.gameserver.seed;

import io.ssafy.p.i13c203.gameserver.global.config.property.AppSeedProperties;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.ImageRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NpcRepository;
import io.ssafy.p.i13c203.gameserver.infra.storage.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class NpcImageSeeder extends AbstractImageSeeder<Npc> implements ApplicationRunner {

    private final AppSeedProperties props;
    private final NpcRepository npcRepository;
    private final ImageRepository imageRepository;

    public NpcImageSeeder(FileStorage storage,
                          AppSeedProperties props,
                          NpcRepository npcRepository,
                          ImageRepository imageRepository) {
        super(storage);
        this.props = props;
        this.npcRepository = npcRepository;
        this.imageRepository = imageRepository;
    }

    @Override protected String groupName() { return "npc"; }

    @Override
    protected SeedProps props() {
        var p = props.getNpc();
        return new SeedProps(Boolean.TRUE.equals(p.getEnabled()), p.getOverwrite(),
                p.getLocation(), p.getMatchBy(), p.getStorage().getDomain());
    }

    @Override
    protected List<Npc> loadTargets(Set<String> possibleKeys) {
        // 파일명 기반으로 좁히고 싶으면 findByCodeIn / findByIdIn 구현해서 사용
        return npcRepository.findAll();
    }

    @Override
    protected String keyOf(Npc e, String matchBy) {
        return "id".equalsIgnoreCase(matchBy) ? String.valueOf(e.getId()) : e.getCode();
    }

    @Override
    protected boolean alreadyLinked(Npc e) {
        return e.getImage() != null;
    }

    @Override
    protected void link(Npc e, UploadMeta m) {

        // 1) storageKey로 먼저 조회
        Image img = imageRepository.findByStorageKey(m.storageKey())
                .orElseGet(() -> {
                    // 2) 없으면 생성 시도
                    Image created = Image.builder()
                            .publicId(m.publicId())          // NOT NULL
                            .storageKey(m.storageKey())      // 자연키로 사용
                            .url(m.url())
                            .contentType(m.contentType())
                            .sizeBytes(m.sizeBytes())        // NOT NULL
                            .originalName(m.originalName())  // 스키마가 NOT NULL이면 필수
                            .status(ImageStatus.ATTACHED)
                            .member(null)                    // 공개 시더면 null
                            .build();
                    try {
                        return imageRepository.save(created);
                    } catch (DataIntegrityViolationException ex) {
                        // 3) 경쟁 저장으로 유니크 제약 위반 시, 다시 조회해서 재사용
                        return imageRepository.findByStorageKey(m.storageKey())
                                .orElseThrow(() -> ex);
                    }
                });

        // 메타 불일치 시 로그만 남기고 그대로 재사용
        if (img.getContentType() != null && m.contentType() != null
                && !img.getContentType().equalsIgnoreCase(m.contentType())) {
            log.warn("Image contentType mismatch for key {}: existing={}, incoming={}",
                    m.storageKey(), img.getContentType(), m.contentType());
        }

        e.setImage(img);
    }

    @Override
    protected void saveAll(List<Npc> batch) {
        npcRepository.saveAll(batch);       // NPC만 save (이미지는 위에서 save 완료)
    }

    @Override
    public void run(ApplicationArguments args) {
        runOnce();
    }
}
