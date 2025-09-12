package io.ssafy.p.i13c203.gameserver.domain.game.doc;

/*
    중분류 가중치
 */
public record EffectWeightsDoc(
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
) {}