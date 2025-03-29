/*
 * Copyright (c) 2025 naturecurly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
