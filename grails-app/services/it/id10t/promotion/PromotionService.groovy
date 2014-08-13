package it.id10t.promotion

import grails.transaction.Transactional

@Transactional
class PromotionService {

    enum Decision {PROMOTE, DEMOTE, NONE, DISHONORABLE_DISCHARGE}

    def promotionRulesEngineService

    def decide(soldier) {

        def traits = promotionRulesEngineService.apply(soldier)

        if (traits.collect{it == "insubordinationDuringBattle"}.count())
            return Decision.DISHONORABLE_DISCHARGE

        if ( traits.collect{it.contains "insubordinationOnLeave"}.count() < 10  && traits.collect{it.contains "heroOnBattlefield"}.count() > 0 )
            return Decision.PROMOTE

        if ( traits.collect{it.contains "insubordinationOnLeave"}.count() > 3  && traits.collect{it.contains "heroOnBattlefield"}.count() == 0 )
            return Decision.DEMOTE

        return Decision.NONE
    }

}
