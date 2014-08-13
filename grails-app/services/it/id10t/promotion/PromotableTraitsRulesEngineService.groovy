package it.id10t.promotion

import grails.transaction.Transactional

@Transactional
class PromotableTraitsRulesEngineService {

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
