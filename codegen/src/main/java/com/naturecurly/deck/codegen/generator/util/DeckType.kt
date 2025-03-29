package com.naturecurly.deck.codegen.generator.util

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
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
            DeckProvider::class.asClassName().parameterizedBy(STAR),
        ),
    )

internal val deckContainerKClassReturnType =
    KClass::class.asClassName().parameterizedBy(
        WildcardTypeName.producerOf(
            DeckContainer::class.asClassName().parameterizedBy(STAR, STAR),
        ),
    )

internal val deckContainerReturnType = DeckContainer::class.asClassName().parameterizedBy(STAR, STAR)

// Pair<DeckContainerUi<*, *>, KClass<out DeckContainer<*, *>>>
internal val deckContainerUiContainerPairReturnType = Pair::class.asClassName().parameterizedBy(
    DeckContainerUi::class.asClassName().parameterizedBy(STAR, STAR),
    deckContainerKClassReturnType,
)
