package com.naturecurly.deck

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DeckDependenciesEntryPoint {
    fun dependencies(): Map<String, @JvmSuppressWildcards Class<out DeckDependencies>>
}

interface DeckDependencies {
    fun providerClass(): KClass<out DeckProvider<*>>
    fun consumers(): Set<@JvmSuppressWildcards DeckConsumer<*, *>>
    fun containerToConsumerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainer<*, *>, KClass<out DeckConsumer<*, *>>>>
}