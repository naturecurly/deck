package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope

interface DeckProvider<OUTPUT> {
    private val consumers: Set<DeckConsumer<OUTPUT, *>>
        get() = Wharf.getDeckConsumers(this::class, System.identityHashCode(this))
    val containers: Map<String, DeckContainer<*, *>>
        get() = Wharf.getDeckContainers(System.identityHashCode(this))

    fun initDeckProvider(scope: CoroutineScope) {
        consumers.forEach { it.init(scope) }
    }

    fun onDeckReady(scope: CoroutineScope, data: OUTPUT) {
        consumers.forEach { it.onDataReady(scope, data) }
    }

    fun onDeckClear() {
        Wharf.clearProvider(System.identityHashCode(this))
    }
}