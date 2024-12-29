package com.naturecurly.deck.codegen.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.naturecurly.deck.codegen.generator.util.deckDependenciesClassName
import com.naturecurly.deck.codegen.generator.util.deckProviderKClassReturnType
import com.naturecurly.deck.codegen.generator.util.getDeckQualifierAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import java.util.Locale

class ProviderModuleGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(
        providerId: String,
        providerClassName: ClassName,
        providerDepsInterfaceClassName: ClassName,
        destinationPackageName: String
    ) {
        val deckModuleName =
            providerId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() } + "DeckModule"

        val classKeyAnnotation = AnnotationSpec.builder(ClassKey::class)
            .addMember("%T::class", providerClassName)
            .build()
        val deckDepsClassReturnType =
            Class::class.asClassName()
                .parameterizedBy(WildcardTypeName.producerOf(deckDependenciesClassName))

        val functionProvideDeckDeps = FunSpec.builder("provideDeckDependencies")
            .addAnnotation(IntoMap::class)
            .addAnnotation(Provides::class)
            .addAnnotation(classKeyAnnotation)
            .returns(deckDepsClassReturnType)
            .addCode("return %T::class.java", providerDepsInterfaceClassName)
            .build()

        val functionProviderKClass = FunSpec.builder("provideDeckProviderKClass")
            .addAnnotation(Provides::class)
            .addAnnotation(getDeckQualifierAnnotation(providerId))
            .returns(deckProviderKClassReturnType)
            .addCode("return %T::class", providerClassName)
            .build()

        val deckModuleType =
            TypeSpec.classBuilder(deckModuleName)
                .addAnnotation(Module::class)
                .addAnnotation(
                    AnnotationSpec.builder(InstallIn::class)
                        .addMember("%T::class", SingletonComponent::class)
                        .build()
                )
                .addFunction(functionProvideDeckDeps)
                .addFunction(functionProviderKClass)
                .build()

        FileSpec.builder(destinationPackageName, deckModuleName)
            .addType(deckModuleType)
            .build()
            .writeTo(codeGenerator, aggregating = false)
    }
}