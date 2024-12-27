package com.naturecurly.deck.compose

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DeckDependenciesEntryPoint {
    fun dependencies(): Map<Class<*>, @JvmSuppressWildcards Class<out DeckDependencies>>
}

interface DeckDependencies {
    fun providerClass(): KClass<out DeckProvider<*>>
    fun consumers(): Set<@JvmSuppressWildcards DeckConsumer<*, *>>
    fun containerToConsumerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainer<*, *>, KClass<out DeckConsumer<*, *>>>>
}