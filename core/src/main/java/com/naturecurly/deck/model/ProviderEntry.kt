package com.naturecurly.deck.model

import com.naturecurly.deck.DeckContainer
import kotlin.reflect.KClass

internal data class ProviderEntry(val identity: Int) {
    val containers: MutableSet<ContainerEntry> = mutableSetOf<ContainerEntry>()

    fun addContainer(container: ContainerEntry) = containers.add(container)
    fun clear(): List<KClass<out DeckContainer<*, *>>> {
        val containerClasses = containers.map { it.clazz }
        containers.clear()
        return containerClasses
    }
}
