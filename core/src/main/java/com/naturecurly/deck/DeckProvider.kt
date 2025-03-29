package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

interface DeckProvider<OUTPUT> {
    private val containers: Set<DeckContainer<OUTPUT, *>>
        get() = WharfLocal.get().getDeckContainers(System.identityHashCode(this))
    val containerUis: Map<String, DeckContainerUi<*, *>>
        get() = WharfLocal.get().getDeckContainerUis(System.identityHashCode(this))

    fun initDeckProvider(scope: CoroutineScope) {
        WharfLocal.get().registerNewProvider<OUTPUT>(this::class, System.identityHashCode(this))
        containers.forEach { it.init(scope) }
        val containersFlow = merge(*(containers.map { it.containerEventFlow }.toTypedArray()))
        scope.launch {
            containersFlow.collect {
                onContainerEvent(it)
            }
        }
    }

    fun onDeckReady(scope: CoroutineScope, data: OUTPUT) {
        containers.forEach { it.onDataReady(scope, data) }
    }

    fun onContainerEvent(containerEvent: ContainerEvent)

    fun onDeckClear() {
        WharfLocal.get().clearProvider(System.identityHashCode(this))
    }
}
