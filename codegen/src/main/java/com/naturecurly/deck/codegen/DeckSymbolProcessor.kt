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
import com.naturecurly.deck.annotations.Consumer
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.annotations.Provider
import com.naturecurly.deck.codegen.generator.ConsumerModuleGenerator
import com.naturecurly.deck.codegen.generator.ContainerModuleGenerator
import com.naturecurly.deck.codegen.generator.ProviderDepsGenerator
import com.naturecurly.deck.codegen.generator.ProviderModuleGenerator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class DeckSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private val providerDepsGenerator by lazy { ProviderDepsGenerator(codeGenerator) }
    private val providerModuleGenerator by lazy { ProviderModuleGenerator(codeGenerator) }
    private val consumerModuleGenerator by lazy { ConsumerModuleGenerator(codeGenerator) }
    private val containerModuleGenerator by lazy { ContainerModuleGenerator(codeGenerator, logger) }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Provider code gen
        val providers = resolver.getSymbolsWithAnnotation(PROVIDER_CLASS_NAME)
        for (provider in providers) {
            if (provider !is KSDeclaration) {
                logger.error("@Provider can't be applied to $provider")
                continue
            }

            if (provider is KSClassDeclaration) {
                val providerId = provider.getAnnotationsByType(Provider::class).firstOrNull()?.id
                val destinationPackageName = provider.packageName.asString() + ".di"
                providerId?.let {
                    val providerDepsClass =
                        providerDepsGenerator.generate(
                            providerId = it,
                            destinationPackageName = destinationPackageName
                        )
                    providerModuleGenerator.generate(
                        providerId = it,
                        providerClassName = provider.toClassName(),
                        providerDepsInterfaceClassName = providerDepsClass,
                        destinationPackageName = destinationPackageName
                    )
                }
            }
        }

        // Consumer code gen
        val consumers = resolver.getSymbolsWithAnnotation(CONSUMER_CLASS_NAME)
        val consumerProviderIdMap = hashMapOf<ClassName, String>()
        for (consumer in consumers) {
            if (consumer !is KSDeclaration) {
                logger.error("@Consumer can't be applied to $consumer")
                continue
            }
            if (consumer is KSClassDeclaration) {
                if (consumer.classKind != ClassKind.CLASS || !hasInjectAnnotation(consumer)) {
                    logger.error("$consumer must be a Class with @Inject constructor()")
                    continue
                }
                val bindTo = consumer.getAnnotationsByType(Consumer::class).firstOrNull()?.bindTo
                val destinationPackageName = consumer.packageName.asString() + ".di"
                bindTo?.let {
                    consumerProviderIdMap[consumer.toClassName()] = bindTo
                    consumerModuleGenerator.generate(
                        providerId = bindTo,
                        consumerClassName = consumer.toClassName(),
                        destinationPackageName = destinationPackageName
                    )
                }
            }
        }

        // Container code gen
        val containers = resolver.getSymbolsWithAnnotation(CONTAINER_CLASS_NAME)
        for (container in containers) {
            if (container !is KSDeclaration) {
                logger.error("@Container can't be applied to $container")
                continue
            }
            if (container is KSClassDeclaration) {
                if (container.classKind != ClassKind.CLASS || !hasInjectAnnotation(container)) {
                    logger.error("$container must be a Class with @Inject constructor()")
                    continue
                }
                val destinationPackageName = container.packageName.asString() + ".di"
                val consumerClass = getConsumerFromContainer(container) as? KSClassDeclaration
                logger.warn("consumerClass: $consumerClass")
                val providerId = consumerProviderIdMap[consumerClass?.toClassName()]
                if (consumerClass != null && providerId != null) {
                    containerModuleGenerator.generate(
                        providerId = providerId,
                        containerClassName = container.toClassName(),
                        consumerClassName = consumerClass.toClassName(),
                        destinationPackageName = destinationPackageName
                    )
                }
            }
        }

        return emptyList()
    }

    private fun hasInjectAnnotation(classDeclaration: KSClassDeclaration): Boolean {
        val constructors = classDeclaration.getConstructors()

        return constructors.any { constructor ->
            constructor.annotations.any { annotation ->
                val annotationName = annotation.shortName.asString()
                val annotationPackage =
                    annotation.annotationType.resolve().declaration.packageName.asString()

                annotationName == "Inject" && (annotationPackage == "javax.inject" || annotationPackage == "jakarta.inject")
            }
        }
    }

    fun getConsumerFromContainer(container: KSClassDeclaration): KSDeclaration? {
        val parentType = container.superTypes
            .map { it.resolve() }
            .firstOrNull { it.declaration.simpleName.asString() == "DeckComposeContainer" }

        return parentType?.arguments?.getOrNull(1)?.type?.resolve()?.declaration
    }

    private companion object {
        val PROVIDER_CLASS_NAME = Provider::class.qualifiedName!!
        val CONSUMER_CLASS_NAME = Consumer::class.qualifiedName!!
        val CONTAINER_CLASS_NAME = Container::class.qualifiedName!!
    }
}