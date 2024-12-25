package com.naturecurly.deck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

abstract class DeckConsumer<INPUT, OUTPUT> {
    abstract fun init(scope: CoroutineScope)
    abstract fun onDataReady(scope: CoroutineScope, data: INPUT)
    abstract val uiStateFlow: StateFlow<OUTPUT>
    abstract fun onEvent(event: Any)
}