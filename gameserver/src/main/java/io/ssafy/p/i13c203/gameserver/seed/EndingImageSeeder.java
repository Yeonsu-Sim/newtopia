package io.ssafy.p.i13c203.gameserver.seed;

import io.ssafy.p.i13c203.gameserver.config.property.AppSeedProperties;
import io.ssafy.p.i13c203.gameserver.domain.ending.entity.Ending;
import io.ssafy.p.i13c203.gameserver.domain.ending.repository.EndingRepository;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.ImageRepository;
import io.ssafy.p.i13c203.gameserver.infra.storage.FileStorage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class EndingImageSeeder extends AbstractImageSeeder<Ending> implements ApplicationRunner {

    private final AppSeedProperties props;
    private final EndingRepository endingRepository;
    private final ImageRepository imageRepository;

    public EndingImageSeeder(FileStorage storage,
                             AppSeedProperties props,
                             EndingRepository endingRepository,
                             ImageRepository imageRepository) {
        super(storage);
        this.props = props;
        this.endingRepository = endingRepository;
        this.imageRepository = imageRepository;
    }

    @Override protected String groupName() { return "ending"; }

    @Override
    protected SeedProps props() {
        var p = props.getEnding();
        return new SeedProps(Boolean.TRUE.equals(p.getEnabled()), p.getOverwrite(),
                p.getLocation(), p.getMatchBy(), p.getStorage().getDomain());
    }

    @Override
    protected List<Ending> loadTargets(Set<String> possibleKeys) {
        // 필요하면 code/id 기반으로 좁히는 쿼리로 교체 가능
        return endingRepository.findAll();
    }

    @Override
    protected String keyOf(Ending e, String matchBy) {
        return "id".equalsIgnoreCase(matchBy) ? String.valueOf(e.getId()) : e.getCode();
    }

    @Override
    protected boolean alreadyLinked(Ending e) {
        return e.getImage() != null;
    }

    @Override
    protected void link(Ending e, UploadMeta m) {
        Image img = Image.builder()
                .publicId(m.publicId())
                .storageKey(m.storageKey())
                .url(m.url())
                .contentType(m.contentType())
                .sizeBytes(m.sizeBytes())
                .originalName(m.originalName())
                .status(ImageStatus.ATTACHED)
                .member(null)
                .build();

        imageRepository.save(img);   // cascade 없으면 명시 저장
        e.setImage(img);             // Ending에 이미지 연결
    }

    @Override
    protected void saveAll(List<Ending> batch) {
        endingRepository.saveAll(batch);
    }

    @Override
    public void run(ApplicationArguments args) {
        runOnce();
    }
}
