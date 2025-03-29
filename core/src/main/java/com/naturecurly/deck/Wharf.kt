package com.naturecurly.deck

import com.naturecurly.deck.model.DeckEntry

abstract class Wharf : ProviderRegister {
    protected val deckEntry = DeckEntry()

    init {
        WharfLocal.init(this)
    }

    internal fun <INPUT> getDeckContainers(
        providerIdentity: Int,
    ): Set<DeckContainer<INPUT, *>> {
        @Suppress("UNCHECKED_CAST")
        return deckEntry.getDeckContainers(providerIdentity).filter { it.isEnabled }
            .toSet() as Set<DeckContainer<INPUT, *>>
    }

    internal fun getDeckContainerUis(
        providerIdentity: Int,
        filterDisabled: Boolean = true,
    ): Map<String, DeckContainerUi<*, *>> {
        return deckEntry.getContainerUisByProvider(providerIdentity, filterDisabled)
            .associate { it.id to it }
    }

    protected fun setContainerToContainerUi(
        containerUi: DeckContainerUi<*, *>,
        container: DeckContainer<*, *>,
    ) {
        containerUi.setContainer(container)
    }

    internal fun clearProvider(providerIdentity: Int) {
        deckEntry.clearProvider(providerIdentity)
    }
}
