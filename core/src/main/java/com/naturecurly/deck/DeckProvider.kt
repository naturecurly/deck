package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

interface DeckProvider<OUTPUT> {
    private val consumers: Set<DeckConsumer<OUTPUT, *>>
        get() = WharfLocal.get().getDeckConsumers(this::class, System.identityHashCode(this))
    val containers: Map<String, DeckContainer<*, *>>
        get() = WharfLocal.get().getDeckContainers(System.identityHashCode(this))

    fun initDeckProvider(scope: CoroutineScope) {
        consumers.forEach { it.init(scope) }
        val consumersFlow = merge(*(consumers.map { it.consumerEventFlow }.toTypedArray()))
        scope.launch {
            consumersFlow.collect {
                onConsumerEvent(it)
            }
        }
    }

    fun onDeckReady(scope: CoroutineScope, data: OUTPUT) {
        consumers.forEach { it.onDataReady(scope, data) }
    }

    fun onConsumerEvent(consumerEvent: ConsumerEvent)

    fun onDeckClear() {
        WharfLocal.get().clearProvider(System.identityHashCode(this))
    }
}
