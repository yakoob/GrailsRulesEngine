package it.id10t.promotion

import it.id10t.rules.IRulesEngine

class PromotionRulesEngineService implements IRulesEngine {

    def promotableTraitsRulesEngineService
    def demotableTraitsRulesEngineService

    def apply( soldier ){

        def traits = []

        traits << promotableTraitsRulesEngineService.apply( soldier )
        traits << demotableTraitsRulesEngineService.apply( soldier )

        return traits

    }

}
