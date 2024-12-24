package com.naturecurly.deck.sample.feature1.di

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckDependencies
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.annotations.DeckQualifier
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MainFeatureDeckDependencies: DeckDependencies {
    @DeckQualifier("MainFeature")
    override fun providerClass(): KClass<out DeckProvider<*>>

    @DeckQualifier("MainFeature")
    override fun consumers(): Set<@JvmSuppressWildcards DeckConsumer<*, *>>

    @DeckQualifier("MainFeature")
    override fun containerToConsumerPairs(): Set<@JvmSuppressWildcards Pair<DeckContainer<*, *>, KClass<out DeckConsumer<*, *>>>>
}