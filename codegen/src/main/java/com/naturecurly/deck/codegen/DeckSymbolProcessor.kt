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

package com.naturecurly.deck.codegen

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.annotations.ContainerUi
import com.naturecurly.deck.annotations.Provider
import com.naturecurly.deck.codegen.generator.ContainerModuleGenerator
import com.naturecurly.deck.codegen.generator.ContainerUiModuleGenerator
import com.naturecurly.deck.codegen.generator.ProviderDepsGenerator
import com.naturecurly.deck.codegen.generator.ProviderModuleGenerator
import com.naturecurly.deck.codegen.generator.util.deckComposeContainerUiClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import javax.inject.Inject

class DeckSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private val providerDepsGenerator by lazy { ProviderDepsGenerator(codeGenerator) }
    private val providerModuleGenerator by lazy { ProviderModuleGenerator(codeGenerator) }
    private val containerModuleGenerator by lazy { ContainerModuleGenerator(codeGenerator) }
    private val containerUiModuleGenerator by lazy { ContainerUiModuleGenerator(codeGenerator, logger) }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        processProviders(resolver)
        processConsumers(resolver)
        processContainers(resolver)
        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun processProviders(resolver: Resolver) {
        val providers = resolver.getSymbolsWithAnnotation(PROVIDER_CLASS_NAME)
        for (provider in providers) {
            if (provider !is KSDeclaration) {
                logger.error("@Provider can't be applied to $provider")
                continue
            }

            if (provider is KSClassDeclaration) {
                val originatingFile = provider.containingFile!!
                val providerId = provider.getAnnotationsByType(Provider::class).firstOrNull()?.id
                val destinationPackageName = provider.packageName.asString() + ".di"
                providerId?.let {
                    val providerDepsClass =
                        providerDepsGenerator.generate(
                            originatingFile = originatingFile,
                            providerId = it,
                            destinationPackageName = destinationPackageName,
                        )
                    providerModuleGenerator.generate(
                        originatingFile = originatingFile,
                        providerId = it,
                        providerClassName = provider.toClassName(),
                        providerDepsInterfaceClassName = providerDepsClass,
                        destinationPackageName = destinationPackageName,
                    )
                }
            }
        }
    }

    @OptIn(KspExperimental::class)
    private fun processConsumers(resolver: Resolver) {
        val consumers = resolver.getSymbolsWithAnnotation(CONTAINER_CLASS_NAME)
        for (consumer in consumers) {
            if (consumer !is KSDeclaration) {
                logger.error("@Consumer can't be applied to $consumer")
                continue
            }
            if (consumer is KSClassDeclaration) {
                val originatingFile = consumer.containingFile!!
                if (consumer.classKind != ClassKind.CLASS || !hasInjectAnnotation(consumer)) {
                    logger.error("$consumer must be a Class with @Inject constructor()")
                    continue
                }
                val bindTo = consumer.getAnnotationsByType(Container::class).firstOrNull()?.bindTo
                val destinationPackageName = consumer.packageName.asString() + ".di"
                bindTo?.let {
                    containerModuleGenerator.generate(
                        originatingFile = originatingFile,
                        providerId = bindTo,
                        containerClassName = consumer.toClassName(),
                        destinationPackageName = destinationPackageName,
                    )
                }
            }
        }
    }

    @OptIn(KspExperimental::class)
    private fun processContainers(resolver: Resolver) {
        val containers = resolver.getSymbolsWithAnnotation(CONTAINER_UI_CLASS_NAME)
        for (container in containers) {
            if (container !is KSDeclaration) {
                logger.error("@Container can't be applied to $container")
                continue
            }
            if (container is KSClassDeclaration) {
                val originatingFile = container.containingFile!!
                if (container.classKind != ClassKind.CLASS || !hasInjectAnnotation(container)) {
                    logger.error("$container must be a Class with @Inject constructor()")
                    continue
                }
                val destinationPackageName = container.packageName.asString() + ".di"
                val consumerClassName = getConsumerFromContainer(container)
                if (consumerClassName == null) {
                    logger.error("$container does not implement a DeckContainer with a valid DeckConsumer")
                    continue
                }
                val providerId = container.getAnnotationsByType(ContainerUi::class).firstOrNull()?.bindTo
                if (providerId.isNullOrBlank()) {
                    logger.error("providerId is not provided.")
                    continue
                }
                containerUiModuleGenerator.generate(
                    originatingFile = originatingFile,
                    providerId = providerId,
                    containerUiClassName = container.toClassName(),
                    containerClassName = consumerClassName,
                    destinationPackageName = destinationPackageName,
                )
            }
        }
    }

    private fun hasInjectAnnotation(classDeclaration: KSClassDeclaration): Boolean {
        return classDeclaration.getConstructors().any { constructor ->
            constructor.annotations.any { annotation ->
                when (annotation.shortName.asString()) {
                    Inject::class.simpleName,
                    jakarta.inject.Inject::class.simpleName,
                    -> annotation.annotationType.resolve().declaration.qualifiedName?.asString()
                        .let { it == Inject::class.qualifiedName || it == jakarta.inject.Inject::class.qualifiedName }

                    else -> false
                }
            }
        }
    }

    fun getConsumerFromContainer(container: KSClassDeclaration): ClassName? {
        for (superType in container.superTypes) {
            val ksType = superType.resolve()
            if (ksType.declaration.qualifiedName?.asString() == deckComposeContainerUiClassName.canonicalName) {
                return (ksType.arguments.getOrNull(1)?.type?.resolve()?.declaration as? KSClassDeclaration)?.toClassName()
            }
        }
        return null
    }

    private companion object {
        val PROVIDER_CLASS_NAME = Provider::class.qualifiedName!!
        val CONTAINER_CLASS_NAME = Container::class.qualifiedName!!
        val CONTAINER_UI_CLASS_NAME = ContainerUi::class.qualifiedName!!
    }
}
