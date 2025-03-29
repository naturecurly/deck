package com.naturecurly.deck.model

import com.naturecurly.deck.DeckContainerUi
import kotlin.reflect.KClass

internal data class ContainerUiEntry(
    val clazz: KClass<out DeckContainerUi<*, *>>,
    val containerUi: DeckContainerUi<*, *>,
    val container: ContainerEntry,
) {
    init {
        container.addContainerUi(this)
    }
}
