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
import com.naturecurly.deck.codegen.generator.ProviderDepInjectionGenerator

class DeckSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private val providerDepInjectionGenerator by lazy { ProviderDepInjectionGenerator(environment.codeGenerator) }

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
                providerId?.let {
                    providerDepInjectionGenerator.generate(it, provider.packageName.asString())
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