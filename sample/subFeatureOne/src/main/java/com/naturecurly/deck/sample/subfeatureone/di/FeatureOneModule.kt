package com.naturecurly.deck.sample.subfeatureone.di

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.annotations.DeckQualifier
import com.naturecurly.deck.sample.subfeatureone.FeatureOneContainer
import com.naturecurly.deck.sample.subfeatureone.FeatureOneDeckConsumer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureOneConsumerModule {
    @Binds
    @IntoSet
    @DeckQualifier("MainFeature")
    abstract fun bindFeatureOneConsumer(consumer: FeatureOneDeckConsumer): DeckConsumer<*, *>
}

@Module
@InstallIn(SingletonComponent::class)
class FeatureOneContainerModule {
    @Provides
    @IntoSet
    @DeckQualifier("MainFeature")
    fun provideFeatureOneContainerConsumerPair(container: FeatureOneContainer): Pair<DeckContainer<*, *>, KClass<out DeckConsumer<*, *>>> =
        container to FeatureOneDeckConsumer::class
}