package com.naturecurly.deck

class DeckScopeImpl(
    override val containers: Map<String, DeckContainer<*, *>>
) : DeckScope