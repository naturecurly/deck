package com.naturecurly.deck.compose

class DeckScopeImpl(
    override val containerUis: Map<String, DeckComposeContainerUi<*, *>>,
) : DeckScope()
