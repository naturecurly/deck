package com.naturecurly.deck.model

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
import kotlin.reflect.KClass

internal data class ContainerEntry(
    val clazz: KClass<out DeckContainer<*, *>>,
    val container: DeckContainer<*, *>,
) {
    val containerUis: MutableSet<ContainerUiEntry> = mutableSetOf<ContainerUiEntry>()

    fun addContainerUi(container: ContainerUiEntry) = containerUis.add(container)
    fun clear(): List<KClass<out DeckContainerUi<*, *>>> {
        val containerUiClasses = containerUis.map { it.clazz }
        containerUis.clear()
        return containerUiClasses
    }
}
