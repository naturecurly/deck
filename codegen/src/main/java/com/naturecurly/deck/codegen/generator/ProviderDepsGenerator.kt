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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import kotlin.reflect.KClass

class ProviderDepsGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(originatingFile: KSFile, providerId: String, destinationPackageName: String): ClassName {
        val deckDependenciesInterfaceName =
            providerId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() } + "DeckDependencies"
        val deckQualifierAnnotation = getDeckQualifierAnnotation(providerId)

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
        val functionProviderClass = FunSpec.builder("providerClass")
            .addOriginatingKSFile(originatingFile)
            .addAnnotation(deckQualifierAnnotation)
            .addModifiers(KModifier.OVERRIDE)
            .addModifiers(KModifier.ABSTRACT)
            .returns(deckProviderKClassReturnType)
            .build()

        val functionContainer = FunSpec.builder("containers")
            .addOriginatingKSFile(originatingFile)
            .addAnnotation(deckQualifierAnnotation)
            .addModifiers(KModifier.OVERRIDE)
            .addModifiers(KModifier.ABSTRACT)
            .returns(containerSetReturnType)
            .build()

        val functionContainerUiToContainerPairs = FunSpec.builder("containerUiToContainerPairs")
            .addOriginatingKSFile(originatingFile)
            .addAnnotation(deckQualifierAnnotation)
            .addModifiers(KModifier.OVERRIDE)
            .addModifiers(KModifier.ABSTRACT)
            .returns(containerUiToContainerPairsReturnType)
            .build()
        // endregion

        val deckDependenciesInterfaceType =
            TypeSpec.interfaceBuilder(deckDependenciesInterfaceName)
                .addOriginatingKSFile(originatingFile)
                .addAnnotation(EntryPoint::class)
                .addAnnotation(
                    AnnotationSpec.builder(InstallIn::class)
                        .addMember("%T::class", SingletonComponent::class)
                        .build(),
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
}
