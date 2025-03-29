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
import com.naturecurly.deck.codegen.generator.util.deckContainerReturnType
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

class ContainerModuleGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(
        originatingFile: KSFile,
        providerId: String,
        containerClassName: ClassName,
        destinationPackageName: String,
    ) {
        val deckModuleName = containerClassName.simpleName + "Module"
        val containerParameter = ParameterSpec.builder("container", containerClassName).build()

        val functionBindContainer = FunSpec.builder("bindContainer")
            .addOriginatingKSFile(originatingFile)
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(IntoSet::class)
            .addAnnotation(Binds::class)
            .addAnnotation(getDeckQualifierAnnotation(providerId))
            .addParameter(containerParameter)
            .returns(deckContainerReturnType)
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
                .addFunction(functionBindContainer)
                .build()

        FileSpec.builder(destinationPackageName, deckModuleName)
            .addType(deckModuleType)
            .build()
            .writeTo(codeGenerator, aggregating = false)
    }
}
