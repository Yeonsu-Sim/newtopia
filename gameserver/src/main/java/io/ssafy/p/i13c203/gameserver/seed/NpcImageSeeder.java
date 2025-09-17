package io.ssafy.p.i13c203.gameserver.seed;

import io.ssafy.p.i13c203.gameserver.config.property.AppSeedProperties;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.ImageRepository;
import io.ssafy.p.i13c203.gameserver.domain.scenario.entity.Npc;
import io.ssafy.p.i13c203.gameserver.domain.scenario.repository.NpcRepository;
import io.ssafy.p.i13c203.gameserver.infra.storage.FileStorage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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
        Image img = Image.builder()
                .publicId(m.publicId())              // NOT NULL 컬럼
                .storageKey(m.storageKey())
                .url(m.url())
                .contentType(m.contentType())
                .sizeBytes(m.sizeBytes())            // NOT NULL 컬럼
                .originalName(m.originalName())      // 스키마가 NOT NULL이면 필수
                .status(ImageStatus.ATTACHED)        // 규칙에 맞춰 READY/ATTACHED 등
                .member(null)                        // 공개 시더면 null
                .build();

        imageRepository.save(img);                   // cascade 없으면 명시 저장
        e.setImage(img);                             // NPC에 연결
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
