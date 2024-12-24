package com.naturecurly.deck

class DeckScopeImpl : DeckScope {
    override val containers: Map<String, DeckContainer<*, *>> =
        Wharf.getDeckContainers()
}