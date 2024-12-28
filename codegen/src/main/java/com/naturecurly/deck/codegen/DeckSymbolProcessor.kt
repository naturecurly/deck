package com.naturecurly.deck.codegen

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.naturecurly.deck.annotations.Provider
import com.naturecurly.deck.codegen.generator.ProviderDeckModuleGenerator
import com.naturecurly.deck.codegen.generator.ProviderDepsGenerator
import com.squareup.kotlinpoet.ksp.toClassName

class DeckSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private val providerDepsGenerator by lazy { ProviderDepsGenerator(codeGenerator) }
    private val providerDeckModuleGenerator by lazy { ProviderDeckModuleGenerator(codeGenerator) }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
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
                    providerDeckModuleGenerator.generate(
                        providerId = it,
                        providerClassName = provider.toClassName(),
                        providerDepsInterfaceClassName = providerDepsClass,
                        destinationPackageName = destinationPackageName
                    )
                }
                logger.warn("${provider.simpleName.asString()} is annotated by @Provider")
            }
        }
        return emptyList()
    }

    private companion object {
        val PROVIDER_CLASS_NAME = Provider::class.qualifiedName!!
    }
}