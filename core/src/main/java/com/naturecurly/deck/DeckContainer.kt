package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class DeckContainer<INPUT, OUTPUT> {
    open val isEnabled: Boolean = true
    abstract fun init(scope: CoroutineScope)
    abstract fun onDataReady(scope: CoroutineScope, data: INPUT)
    abstract val uiStateFlow: StateFlow<OUTPUT>
    abstract fun <Event> onEvent(event: Event)
    private val _containerEventFlow: MutableSharedFlow<ContainerEvent> = MutableSharedFlow()
    internal val containerEventFlow: Flow<ContainerEvent> = _containerEventFlow
    fun notifyProvider(event: ContainerEvent, scope: CoroutineScope) {
        scope.launch {
            _containerEventFlow.emit(event)
        }
    }
}
