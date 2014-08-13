GrailsRulesEngine
=================

Rules Engine for Groovy &amp; Grails

This is a simple example of a convention over configuration Rules Engine in Grooy & Grails.

Goals:
<ul>
	<li>Extendable engine that can be used in any scenario.</li>
	<li>Typeless rules that can be added or removed at runtime. "convention over configuration"</li>
	<li>Decoupled decision that can ask for 1 or more rules engine results.</li>
</ul>

<p>Design with soldier promotion implementation</p>
<img src="http://www.yakoobahmad.com/images/blog/groovy_grails_rules_engine.png">
<p>Lets start with an interface to define the basic contract for a rules engine.</p>
<pre class="prettyprint">
public interface IRulesEngine {
    def apply(obj)
}
</pre>

<p>Here is a concrete base implementation of our rules engine. All of our business derived rules engines will extend this.  Methods with the word "Rule" at the end will be executed when calling the apply() method.</p>
<pre class="prettyprint">
class RulesEngine implements IRulesEngine {

    def getRules() {
        def rules = []
        this.class.declaredFields.each {
            def field = this."${it.name}"
            if (!it.isSynthetic() && field instanceof Closure && it.name.endsWith("Rule")) {
                rules << it.name
            }
        }
        rules
    }

    def apply(obj) {
        Set responseSet = [] as Set
        rules.each { rule ->
            responseSet << this."$rule"(obj)
        }
        responseSet
    }
}
</pre>

<p>We can start off with 2 engines.  Ones which return promotable and demote able traits.<p>

<pre class="prettyprint">
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
</pre>

<pre class="prettyprint">
class DemotableTraitsRulesEngineService extends RulesEngine {

    // example of a rule that returns many traits
    def insubordinationOnLeaveRule = { soldier ->

        def traits = []

        log.info("insubordinationOnLeaveRule() ran")

        soldier.leaves.findAll{it.insubordination == true}.each { leave ->
            traits << """insubordinationOnLeave - ${leave.country} ${leave.city} ${leave.place}"""
        }

        return traits
    }

    // example of a rule that returns 1 traits
    def insubordinationDuringBattleRule = { soldier ->

        def traits = []

        log.info("insubordinationDuringBattleRule() ran")

        def hasInsubordinationDuringBattleRule = War*.campaigns*.battles*.insubordinates.find { insubordinate ->
            insubordinate.soldier == soldier
        }

        if (hasInsubordinationDuringBattleRule)
            traits << "insubordinationDuringBattle"

        return traits

    }
}
</pre>

<p>Now we can create a service that aggregates and applies these traits.</p>
<pre class="prettyprint">
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
</pre>

<p>Lets get on to our decision already...</p>
<pre class="prettyprint">
class PromotionService {

    enum Decision {PROMOTE, DEMOTE, NONE, DISHONORABLE_DISCHARGE}

    def promotionRulesEngineService

    def decide(soldier) {

        def traits = promotionRulesEngineService.apply(soldier)

        if (traits.collect{it == "insubordinationDuringBattle"}.count())
            return Decision.DISHONORABLE_DISCHARGE

        if (traits.collect{it.contains "insubordinationOnLeave"}.count() < 10  
		&& traits.collect{it.contains "heroOnBattlefield"}.count() > 0 )
            return Decision.PROMOTE

        if (traits.collect{it.contains "insubordinationOnLeave"}.count() > 3  
		&& traits.collect{it.contains "heroOnBattlefield"}.count() == 0 )
            return Decision.DEMOTE

        return Decision.NONE
    }

}
</pre>

I hope you enjoyed this solution.  You can clone it <a target="_blank" href="https://github.com/yakoob/GrailsRulesEngine">here</a> on github
