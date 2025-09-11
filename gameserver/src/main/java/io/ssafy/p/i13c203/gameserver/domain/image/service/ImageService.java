package io.ssafy.p.i13c203.gameserver.domain.image.service;

import io.ssafy.p.i13c203.gameserver.domain.image.dto.response.ImageUploadResponse;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.Image;
import io.ssafy.p.i13c203.gameserver.domain.image.entity.ImageStatus;
import io.ssafy.p.i13c203.gameserver.domain.image.repository.ImageRepository;
import io.ssafy.p.i13c203.gameserver.domain.member.repository.MemberRepository;
import io.ssafy.p.i13c203.gameserver.global.utils.StorageKeyBuilder;
import io.ssafy.p.i13c203.gameserver.infra.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileStore;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final FileStorage storage;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

//    storageKey = u/{memberId}/{domain}/{yyyy}/{MM}/{dd}/{uuid}.{ext} 형식으로 생성
//    로컬 또는 S3에 저장 후 previewUrl 생성
//    image_file 테이블에 status=TEMP로 insert
//    deleteIfTempOwnedBy는 status=TEMP인 경우만 삭제(or 삭제 예약)

    @Transactional
    public ImageUploadResponse upload(MultipartFile file, Long memberId, String domain) throws IOException {

        // 식별자/확장자/키 생성
        UUID fileId = UUID.randomUUID(); //UUID
        String ext = StorageKeyBuilder.extOf(file.getOriginalFilename(), file.getContentType());
        String key = StorageKeyBuilder.build(memberId, domain, fileId, ext);



        // 2 저장소에 업로드 (키를 지정)

        storage.store(key, file);

        // 3 DB에 메타 저장 (TEMP)

        Image image = Image.builder()
                .publicId(fileId)
                .storageKey(key)
                .status(ImageStatus.TEMP)
                .sizeBytes(file.getSize())
                .contentType(file.getContentType())
                .originalName(file.getOriginalFilename())
                .member(memberRepository.findById(memberId).orElseThrow())
                .build();

        String url = storage.publicUrl(key);


        Image save = imageRepository.save(image);



        return new ImageUploadResponse(
                save.getId(),

                image.getPublicId(), url, image.getStorageKey(),
                image.getContentType(), image.getSizeBytes(), image.getOriginalName(), image.getStatus().name()
        );    }
}
