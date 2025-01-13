package com.naturecurly.deck.compose

class DeckScopeImpl(
    override val containers: Map<String, DeckComposeContainer<*, *>>,
) : DeckScope()
