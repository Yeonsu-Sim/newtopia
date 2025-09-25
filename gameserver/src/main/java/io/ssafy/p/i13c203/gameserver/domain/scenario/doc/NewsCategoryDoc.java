package io.ssafy.p.i13c203.gameserver.domain.scenario.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsCategoryDoc(
        @JsonProperty("sub_categories")
        Map<String, List<CategoryConfidence>> subCategories,

        @JsonProperty("major_categories")
        List<CategoryConfidence> majorCategories,

        @JsonProperty("debug_similarities")
        Map<String, Double> debugSimilarities
) {
}

