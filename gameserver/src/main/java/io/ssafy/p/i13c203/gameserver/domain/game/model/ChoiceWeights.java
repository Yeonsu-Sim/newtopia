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
public class ChoiceWeights {
    // economy
    private double macroeconomy;
    private double fiscalPolicy;
    private double financialMarkets;
    private double industryBusiness;
    // defense
    private double militarySecurity;
    private double alliances;
    private double cyberSpace;
    private double publicSafety;
    // publicSentiment
    private double publicOpinion;
    private double socialIssues;
    private double protestsStrikes;
    private double healthWelfare;
    // environment
    private double climateChangeEnergy;
    private double pollutionDisaster;
    private double biodiversity;
    private double resourceManagement;


    public void add(ChoiceWeights delta) {
        if (delta == null) return;
        this.macroeconomy = clamp0To1(this.macroeconomy + delta.macroeconomy);
        this.fiscalPolicy = clamp0To1(this.fiscalPolicy + delta.fiscalPolicy);
        this.financialMarkets = clamp0To1(this.financialMarkets + delta.financialMarkets);
        this.industryBusiness = clamp0To1(this.industryBusiness + delta.industryBusiness);
        this.militarySecurity = clamp0To1(this.militarySecurity + delta.militarySecurity);
        this.alliances = clamp0To1(this.alliances + delta.alliances);
        this.cyberSpace = clamp0To1(this.cyberSpace + delta.cyberSpace);
        this.publicSafety = clamp0To1(this.publicSafety + delta.publicSafety);
        this.publicOpinion = clamp0To1(this.publicOpinion + delta.publicOpinion);
        this.socialIssues = clamp0To1(this.socialIssues + delta.socialIssues);
        this.protestsStrikes = clamp0To1(this.protestsStrikes + delta.protestsStrikes);
        this.healthWelfare = clamp0To1(this.healthWelfare + delta.healthWelfare);
        this.climateChangeEnergy = clamp0To1(this.climateChangeEnergy + delta.climateChangeEnergy);
        this.pollutionDisaster = clamp0To1(this.pollutionDisaster + delta.pollutionDisaster);
        this.biodiversity = clamp0To1(this.biodiversity + delta.biodiversity);
        this.resourceManagement = clamp0To1(this.resourceManagement + delta.resourceManagement);
    }

    private double clamp0To1(double v) { return Math.max(0.0, Math.min(1.0, v)); }

    // 대분류별 합산 (규칙: 그룹 내 4개 합)
    public double sumEconomy() {
        return macroeconomy + fiscalPolicy + financialMarkets + industryBusiness;
    }
    public double sumDefense() {
        return militarySecurity + alliances + cyberSpace + publicSafety;
    }
    public double sumPublicSentiment() {
        return publicOpinion + socialIssues + protestsStrikes + healthWelfare;
    }
    public double sumEnvironment() {
        return climateChangeEnergy + pollutionDisaster + biodiversity + resourceManagement;
    }
}
