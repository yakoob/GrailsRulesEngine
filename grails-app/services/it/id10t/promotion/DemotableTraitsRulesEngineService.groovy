package it.id10t.promotion

import grails.transaction.Transactional
import it.id10t.rules.RulesEngine

@Transactional(readOnly = true)
class DemotableTraitsRulesEngineService extends RulesEngine {

    // example of a rule that returns many traits
    def insubordinationOnLeaveRule = { soldier ->

        def traits = []

        log.info("insubordinationOnLeaveRule() ran")

        // some logic here to find out if your soldier is insubordinate on leave
        soldier.leaves.findAll{it.insubordination == true}.each { leave ->
            traits << """insubordinationOnLeave - ${leave.country} ${leave.city} ${leave.place}"""
        }

        return traits
    }

    // example of a rule that returns 1 traits
    def insubordinationDuringBattleRule = { soldier ->

        def traits = []

        log.info("insubordinationDuringBattleRule() ran")

        // some logic here to find out if your soldier is insubordinate during battle
        def hasInsubordinationDuringBattleRule = War*.campaigns*.battles*.insubordinates.find { insubordinate ->
            insubordinate.soldier == soldier
        }

        if (hasInsubordinationDuringBattleRule)
            traits << "insubordinationDuringBattle"

        return traits

    }

}
