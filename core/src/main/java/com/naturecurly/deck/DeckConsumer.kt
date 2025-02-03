package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class DeckConsumer<INPUT, OUTPUT> {
    abstract fun init(scope: CoroutineScope)
    abstract fun onDataReady(scope: CoroutineScope, data: INPUT)
    abstract val uiStateFlow: StateFlow<OUTPUT>
    abstract fun <Event> onEvent(event: Event)
    private val _consumerEventFlow: MutableSharedFlow<ConsumerEvent> = MutableSharedFlow()
    internal val consumerEventFlow: Flow<ConsumerEvent> = _consumerEventFlow
    fun notifyProvider(event: ConsumerEvent, scope: CoroutineScope) {
        scope.launch {
            _consumerEventFlow.emit(event)
        }
    }
}
