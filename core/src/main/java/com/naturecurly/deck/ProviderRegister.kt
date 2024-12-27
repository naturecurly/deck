package com.naturecurly.deck

import kotlin.reflect.KClass

interface ProviderRegister {
    fun <INPUT> registerNewProvider(
        providerClass: KClass<out DeckProvider<*>>,
        providerIdentity: Int,
    )
}