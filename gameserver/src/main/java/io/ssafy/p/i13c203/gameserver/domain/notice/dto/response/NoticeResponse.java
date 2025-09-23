package io.ssafy.p.i13c203.gameserver.domain.notice.dto.response;

import io.ssafy.p.i13c203.gameserver.domain.notice.entity.Notice;
import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private NoticeType type;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoticeResponse from(Notice notice) {

        String imgUrl = "https://j13c203.p.ssafy.io/newtopia-img/public/public/2025/09/23/69df6829-9414-4c7e-bdc5-7da5f07533e5.png";

        NoticeType type1 = notice.getType();
        if(type1 == NoticeType.NOTICE){
            imgUrl = "https://j13c203.p.ssafy.io/newtopia-img/public/public/2025/09/23/69df6829-9414-4c7e-bdc5-7da5f07533e5.png";
        }else if(type1 == NoticeType.UPDATE){
            imgUrl = "https://j13c203.p.ssafy.io/newtopia-img/public/public/2025/09/23/cb232b52-926b-429a-9648-7d5be9380935.png";
        }else if (type1 == NoticeType.HOTFIX){
            imgUrl = "https://j13c203.p.ssafy.io/newtopia-img/public/public/2025/09/23/518eb180-8b36-4e78-a489-52167577a875.png";
        }


        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .type(notice.getType())
                .imgUrl(imgUrl)
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}