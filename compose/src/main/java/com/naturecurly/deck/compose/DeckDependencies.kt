package com.naturecurly.deck.compose

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
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
    fun containers(): Set<@JvmSuppressWildcards DeckContainer<*, *>>
    fun containerUiToContainerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainerUi<*, *>, KClass<out DeckContainer<*, *>>>>
}
