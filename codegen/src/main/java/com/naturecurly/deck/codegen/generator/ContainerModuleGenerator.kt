package com.naturecurly.deck.codegen.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.naturecurly.deck.codegen.generator.util.deckContainerConsumerPairReturnType
import com.naturecurly.deck.codegen.generator.util.getDeckQualifierAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

class ContainerModuleGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) {
    fun generate(
        originatingFile: KSFile,
        providerId: String,
        containerClassName: ClassName,
        consumerClassName: ClassName,
        destinationPackageName: String,
    ) {
        val deckModuleName = containerClassName.simpleName + "Module"
        val containerParameter = ParameterSpec.builder("container", containerClassName).build()

        val functionProvideConsumer = FunSpec.builder("provideContainer")
            .addOriginatingKSFile(originatingFile)
            .addAnnotation(IntoSet::class)
            .addAnnotation(Provides::class)
            .addAnnotation(getDeckQualifierAnnotation(providerId))
            .addParameter(containerParameter)
            .returns(deckContainerConsumerPairReturnType)
            .addCode("return container to %T::class", consumerClassName)
            .build()

        val deckModuleType =
            TypeSpec.classBuilder(deckModuleName)
                .addOriginatingKSFile(originatingFile)
                .addAnnotation(Module::class)
                .addAnnotation(
                    AnnotationSpec.builder(InstallIn::class)
                        .addMember("%T::class", SingletonComponent::class)
                        .build(),
                )
                .addFunction(functionProvideConsumer)
                .build()

        FileSpec.builder(destinationPackageName, deckModuleName)
            .addType(deckModuleType)
            .build()
            .writeTo(codeGenerator, aggregating = false)
    }
}
