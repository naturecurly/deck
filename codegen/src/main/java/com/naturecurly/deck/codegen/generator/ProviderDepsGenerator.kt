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

package com.naturecurly.deck.codegen.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi
import com.naturecurly.deck.codegen.generator.util.deckDependenciesClassName
import com.naturecurly.deck.codegen.generator.util.deckProviderKClassReturnType
import com.naturecurly.deck.codegen.generator.util.getDeckQualifierAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import jakarta.inject.Inject
import java.util.Locale
import kotlin.reflect.KClass

class ProviderDepsGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(
        originatingFile: KSFile,
        providerId: String,
        destinationPackageName: String,
    ): ClassName {
        val deckDependenciesInterfaceName =
            providerId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() } + "DeckDependencies"

        // region Return Type
        val containerSetReturnType = Set::class.asClassName().parameterizedBy(
            DeckContainer::class.asClassName().parameterizedBy(STAR, STAR).copy(
                annotations = listOf(AnnotationSpec.builder(JvmSuppressWildcards::class).build()),
            ),
        )
        val containerUiToContainerPairsReturnType = Set::class.asClassName().parameterizedBy(
            Pair::class.asClassName().parameterizedBy(
                DeckContainerUi::class.asClassName().parameterizedBy(STAR, STAR),
                KClass::class.asClassName().parameterizedBy(
                    WildcardTypeName.producerOf(
                        DeckContainer::class.asClassName().parameterizedBy(STAR, STAR),
                    ),
                ),
            ).copy(
                annotations = listOf(
                    AnnotationSpec.builder(JvmSuppressWildcards::class).build(),
                ),
            ),
        )
        // endregion

        // region Functions
        val providerClassPropertyName = "providerClass"
        val functionProviderClass = FunSpec.builder(providerClassPropertyName)
            .addOriginatingKSFile(originatingFile)
            .addModifiers(KModifier.OVERRIDE)
            .returns(deckProviderKClassReturnType)
            .addStatement("return $providerClassPropertyName")
            .build()

        val containerPropertyName = "containers"
        val functionContainer = FunSpec.builder(containerPropertyName)
            .addOriginatingKSFile(originatingFile)
            .addModifiers(KModifier.OVERRIDE)
            .returns(containerSetReturnType)
            .addStatement("return $containerPropertyName")
            .build()

        val containerUiPairPropertyName = "containerUiToContainerPairs"
        val functionContainerUiToContainerPairs = FunSpec.builder("containerUiToContainerPairs")
            .addOriginatingKSFile(originatingFile)
            .addModifiers(KModifier.OVERRIDE)
            .returns(containerUiToContainerPairsReturnType)
            .addStatement("return $containerUiPairPropertyName")
            .build()
        // endregion

        val deckDependenciesInterfaceType =
            TypeSpec.classBuilder(deckDependenciesInterfaceName)
                .addOriginatingKSFile(originatingFile)
                .constructor(
                    providerId,
                    mapOf(
                        providerClassPropertyName to deckProviderKClassReturnType,
                        containerPropertyName to containerSetReturnType,
                        containerUiPairPropertyName to containerUiToContainerPairsReturnType,
                    ),
                )
                .addSuperinterface(deckDependenciesClassName)
                .addFunction(functionProviderClass)
                .addFunction(functionContainer)
                .addFunction(functionContainerUiToContainerPairs)
                .build()

        val deckDependenciesInterfaceClassName =
            ClassName(destinationPackageName, deckDependenciesInterfaceName)
        FileSpec.builder(deckDependenciesInterfaceClassName)
            .addType(deckDependenciesInterfaceType)
            .build()
            .writeTo(codeGenerator, aggregating = false)
        return deckDependenciesInterfaceClassName
    }

    private fun TypeSpec.Builder.constructor(
        providerId: String,
        properties: Map<String, ParameterizedTypeName>,
    ): TypeSpec.Builder {
        val deckQualifierAnnotation = getDeckQualifierAnnotation(providerId)

        return this.primaryConstructor(
            FunSpec.constructorBuilder()
                .addAnnotation(Inject::class.asClassName())
                .apply {
                    properties.forEach { name, type ->
                        addParameter(name, type)
                    }
                }
                .build(),
        ).addProperties(
            properties.map {
                PropertySpec.builder(it.key, it.value)
                    .initializer(it.key)
                    .addAnnotation(deckQualifierAnnotation)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            },
        )
    }
}
