package com.naturecurly.deck.codegen.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.naturecurly.deck.codegen.generator.util.deckConsumerReturnType
import com.naturecurly.deck.codegen.generator.util.getDeckQualifierAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Locale

class ConsumerModuleGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(
        originatingFile: KSFile,
        providerId: String,
        consumerClassName: ClassName,
        destinationPackageName: String,
    ) {
        val deckModuleName =
            providerId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() } + "ConsumerModule"
        val consumerParameter = ParameterSpec.builder("consumer", consumerClassName).build()

        val functionBindConsumer = FunSpec.builder("bindConsumer")
            .addOriginatingKSFile(originatingFile)
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(IntoSet::class)
            .addAnnotation(Binds::class)
            .addAnnotation(getDeckQualifierAnnotation(providerId))
            .addParameter(consumerParameter)
            .returns(deckConsumerReturnType)
            .build()

        val deckModuleType =
            TypeSpec.classBuilder(deckModuleName)
                .addOriginatingKSFile(originatingFile)
                .addModifiers(KModifier.ABSTRACT)
                .addAnnotation(Module::class)
                .addAnnotation(
                    AnnotationSpec.builder(InstallIn::class)
                        .addMember("%T::class", SingletonComponent::class)
                        .build(),
                )
                .addFunction(functionBindConsumer)
                .build()

        FileSpec.builder(destinationPackageName, deckModuleName)
            .addType(deckModuleType)
            .build()
            .writeTo(codeGenerator, aggregating = false)
    }
}
