package com.naturecurly.deck.model

import com.naturecurly.deck.DeckContainer
import kotlin.reflect.KClass

internal data class ContainerEntry(
    val clazz: KClass<out DeckContainer<*, *>>,
    val container: DeckContainer<*, *>,
    val consumer: ConsumerEntry
) {
    init {
        consumer.addContainer(this)
    }
}