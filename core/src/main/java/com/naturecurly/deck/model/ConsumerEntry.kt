package com.naturecurly.deck.model

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import kotlin.reflect.KClass

internal data class ConsumerEntry(
    val clazz: KClass<out DeckConsumer<*, *>>,
    val consumer: DeckConsumer<*, *>
) {
    val containers: Set<ContainerEntry>
        field = mutableSetOf<ContainerEntry>()

    fun addContainer(container: ContainerEntry) = containers.add(container)
    fun clear(): List<KClass<out DeckContainer<*, *>>> {
        val containerClasses = containers.map { it.clazz }
        containers.clear()
        return containerClasses
    }
}
