package io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.mapper;

import io.ssafy.p.i13c203.gameserver.domain.gameresult.doc.SummaryDoc;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.dto.response.SummaryDto;
import io.ssafy.p.i13c203.gameserver.domain.gameresult.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class GameResultSummaryDtoMapper {

    public SummaryDto toSummary(SummaryDoc doc) {
        return new SummaryDto(
                toStatusString(doc.status()),
                doc.promptHash(),
                toSections(doc.sections()),
                doc.subscribeUrl()
        );
    }

    public SummaryDto toReadySummary(SummaryDoc doc) {
        return new SummaryDto(
                toStatusString(SummaryStatus.READY),
                null,
                toSections(doc.sections()),
                null
        );
    }

    public SummaryDto.Sections toSections(SummarySections sections) {
        if (sections == null || sections.getBlocks() == null || sections.getBlocks().isEmpty()) {
            return null;
        }

        Map<String, SummaryDto.Block> dto = new LinkedHashMap<>();

        sections.getBlocks().forEach((key, block) -> {
            switch (block.type()) {
                case BULLETS -> {
                    if (block instanceof BulletsBlock bullets) {
                        dto.put(key, new SummaryDto.BulletsBlock(
                                "BULLETS",
                                bullets.title(),
                                bullets.bullets()
                        ));
                    } else {
                        log.error("Expected BulletsBlock but got {}", block.getClass().getSimpleName());
                    }
                }
                case TEXT -> {
                    if (block instanceof TextBlock text) {
                        dto.put(key, new SummaryDto.RichTextBlock(
                                "TEXT",
                                text.title(),
                                text.text()
                        ));
                    } else {
                        log.error("Expected TextBlock but got {}", block.getClass().getSimpleName());
                    }
                }
                case RICH_TEXT -> {
                    if (block instanceof RichTextBlock rich) {
                        dto.put(key, new SummaryDto.RichTextBlock(
                                "RICH_TEXT",
                                rich.title(),
                                rich.richText()
                        ));
                    } else {
                        log.error("Expected RichTextBlock but got {}", block.getClass().getSimpleName());
                    }
                }
                default -> {
                    // 확장 대비
                    log.warn("Unsupported block type [{}] for key={}", block.type(), key);
                }
            }
        });

        return new SummaryDto.Sections(dto);
    }


    private String toStatusString(SummaryStatus st) {
        // Enum 노출 정책을 여기서 통일
        return st == null ? SummaryStatus.PENDING.name() : st.name();
    }
}
