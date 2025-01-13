package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope

interface DeckProvider<OUTPUT> {
    private val consumers: Set<DeckConsumer<OUTPUT, *>>
        get() = WharfLocal.get().getDeckConsumers(this::class, System.identityHashCode(this))
    val containers: Map<String, DeckContainer<*, *>>
        get() = WharfLocal.get().getDeckContainers(System.identityHashCode(this))

    fun initDeckProvider(scope: CoroutineScope) {
        consumers.forEach { it.init(scope) }
    }

    fun onDeckReady(scope: CoroutineScope, data: OUTPUT) {
        consumers.forEach { it.onDataReady(scope, data) }
    }

    fun onDeckClear() {
        WharfLocal.get().clearProvider(System.identityHashCode(this))
    }
}
