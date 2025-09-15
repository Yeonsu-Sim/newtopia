package io.ssafy.p.i13c203.gameserver.domain.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;



@AllArgsConstructor
@Getter
@Setter
public class ImageUploadResponse {
    Long imageId;
    UUID publicId;
    String url;
    String storageKey;
    String contentType;
    Long sizeBytes;
    String originalName;
    String status;

}
