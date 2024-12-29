package com.naturecurly.deck.codegen.generator.util

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckProvider
import com.naturecurly.deck.annotations.DeckQualifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KClass

internal fun getDeckQualifierAnnotation(providerId: String) =
    AnnotationSpec.builder(DeckQualifier::class)
        .addMember("%S", providerId)
        .build()

internal val deckProviderKClassReturnType =
    KClass::class.asClassName().parameterizedBy(
        WildcardTypeName.producerOf(
            DeckProvider::class.asClassName().parameterizedBy(STAR)
        )
    )

internal val deckConsumerKClassReturnType =
    KClass::class.asClassName().parameterizedBy(
        WildcardTypeName.producerOf(
            DeckConsumer::class.asClassName().parameterizedBy(STAR, STAR)
        )
    )

internal val deckConsumerReturnType = DeckConsumer::class.asClassName().parameterizedBy(STAR, STAR)

// Pair<DeckContainer<*, *>, KClass<out DeckConsumer<*, *>>>
internal val deckContainerConsumerPairReturnType = Pair::class.asClassName().parameterizedBy(
    DeckContainer::class.asClassName().parameterizedBy(STAR, STAR),
    deckConsumerKClassReturnType
)