package io.ssafy.p.i13c203.gameserver.domain.game.doc;

import io.ssafy.p.i13c203.gameserver.domain.game.model.ChoiceWeights;

public record ChoiceWeightsDoc(

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
    public static ChoiceWeightsDoc from(ChoiceWeights w) {
        return new ChoiceWeightsDoc(
                w.getMacroeconomy(), w.getFiscalPolicy(), w.getFinancialMarkets(), w.getIndustryBusiness(),
                w.getMilitarySecurity(), w.getAlliances(), w.getCyberSpace(), w.getPublicSafety(),
                w.getPublicOpinion(), w.getSocialIssues(), w.getProtestsStrikes(), w.getHealthWelfare(),
                w.getClimateChangeEnergy(), w.getPollutionDisaster(), w.getBiodiversity(), w.getResourceManagement()
        );
    }
}
