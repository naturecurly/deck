package com.naturecurly.deck.sample.feature1.di

import com.naturecurly.deck.DeckDependencies
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.annotations.DeckQualifier
import com.naturecurly.deck.sample.feature1.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
class MainFeatureDeckModule {
    @IntoMap
    @Provides
    @StringKey("MainFeature")
    fun provideMainFeatureDeckDependencies(): Class<out DeckDependencies> =
        MainFeatureDeckDependencies::class.java

    @Provides
    @DeckQualifier("MainFeature")
    fun provideDeckProviderKClass(): KClass<out DeckProvider<*>> = MainViewModel::class
}