package io.ssafy.p.i13c203.gameserver.domain.game.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryStats {

    @Builder.Default
    private int economy = 50; // 경제

    @Builder.Default
    private int defense = 50; // 국방

    @Builder.Default
    private int publicSentiment = 50; // 여론/사회 인식

    @Builder.Default
    private int environment = 50; // 환경

    public void addDelta(int dEconomy, int dDefense, int dPublicSentiment, int dEnvironment) {
        this.economy = clamp0To100(this.economy + dEconomy);
        this.defense = clamp0To100(this.defense + dDefense);
        this.publicSentiment = clamp0To100(this.publicSentiment + dPublicSentiment);
        this.environment = clamp0To100(this.environment + dEnvironment);
    }
    public void setValue(int aEconomy, int aDefense, int aPublicSentiment, int aEnvironment) {
        this.economy = aEconomy;
        this.defense = aDefense;
        this.publicSentiment = aPublicSentiment;
        this.environment = aEnvironment;
    }


    /***** HELPER *****/
    private int clamp0To100(int v) { return Math.max(0, Math.min(100, v)); }
}