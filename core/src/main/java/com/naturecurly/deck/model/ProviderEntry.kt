package com.naturecurly.deck.model

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckProvider
import kotlin.reflect.KClass

internal data class ProviderEntry(val clazz: KClass<out DeckProvider<*>>) {
    val consumers: MutableSet<ConsumerEntry> = mutableSetOf<ConsumerEntry>()

    fun addConsumer(consumer: ConsumerEntry) = consumers.add(consumer)
    fun clear(): List<KClass<out DeckConsumer<*, *>>> {
        val consumerClasses = consumers.map { it.clazz }
        consumers.clear()
        return consumerClasses
    }
}