package it.id10t.rules

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