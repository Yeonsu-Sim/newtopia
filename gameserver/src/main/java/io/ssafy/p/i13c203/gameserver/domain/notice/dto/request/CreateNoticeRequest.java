package io.ssafy.p.i13c203.gameserver.domain.notice.dto.request;

import io.ssafy.p.i13c203.gameserver.domain.notice.entity.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoticeRequest {
    private String title;
    private String content;
    private NoticeType type;
}