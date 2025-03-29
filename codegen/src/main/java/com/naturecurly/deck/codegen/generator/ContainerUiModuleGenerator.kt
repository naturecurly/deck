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
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.naturecurly.deck.codegen.generator.util.deckContainerUiContainerPairReturnType
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

class ContainerUiModuleGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) {
    fun generate(
        originatingFile: KSFile,
        providerId: String,
        containerUiClassName: ClassName,
        containerClassName: ClassName,
        destinationPackageName: String,
    ) {
        val deckModuleName = containerUiClassName.simpleName + "Module"
        val containerUiParameter = ParameterSpec.builder("containerUi", containerUiClassName).build()

        val functionProvideContainer = FunSpec.builder("provideContainer")
            .addOriginatingKSFile(originatingFile)
            .addAnnotation(IntoSet::class)
            .addAnnotation(Provides::class)
            .addAnnotation(getDeckQualifierAnnotation(providerId))
            .addParameter(containerUiParameter)
            .returns(deckContainerUiContainerPairReturnType)
            .addCode("return containerUi to %T::class", containerClassName)
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
                .addFunction(functionProvideContainer)
                .build()

        FileSpec.builder(destinationPackageName, deckModuleName)
            .addType(deckModuleType)
            .build()
            .writeTo(codeGenerator, aggregating = false)
    }
}
