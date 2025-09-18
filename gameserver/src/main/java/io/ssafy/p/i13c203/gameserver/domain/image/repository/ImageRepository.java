package io.ssafy.p.i13c203.gameserver.domain.image.repository;

import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByStorageKey(String storageKey);
}
