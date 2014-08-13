package it.id10t.promotion

import grails.transaction.Transactional
import it.id10t.rules.RulesEngine

@Transactional
class PromotableTraitsRulesEngineService extends RulesEngine {

    // example of a rule that returns many traits
    def heroOnBattlefieldRule = { soldier ->

        def traits = []

        log.info("heroOnBattlefieldRule() ran")

        War*.campaigns*.battles.collect { battle -> battle.heros in soldier}.each { battle ->
            traits << """heroOnBattlefield - ${battle.campaign.war} ${battle.campaign}"""
        }

        return traits
    }

}
