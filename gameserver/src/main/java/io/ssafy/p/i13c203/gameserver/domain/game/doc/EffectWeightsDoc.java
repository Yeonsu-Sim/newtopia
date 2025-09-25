package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.scenario.model.EffectApplyType;

/*
    중분류 가중치
 */
public record EffectWeightsDoc(
        EffectApplyType applyType,
        double macroeconomy,
        double fiscalPolicy,
        double financialMarkets,
        double industryBusiness,

        double militarySecurity,
        double alliances,
        double cyberSpace,
        double publicSafety,

        double publicOpinion,
        double socialIssues,
        double protestsStrikes,
        double healthWelfare,

        double climateChangeEnergy,
        double pollutionDisaster,
        double biodiversity,
        double resourceManagement
) {

    public EffectWeightsDoc {
        if (applyType == null) {
            applyType = EffectApplyType.RELATIVE;  // 기본값
        }
    }
}