package com.naturecurly.deck.sample.subfeatureone

import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.annotations.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Consumer(bindTo = "MainFeature")
class FeatureOneDeckConsumer @Inject constructor() : DeckConsumer<String, String>() {
    private val _uiStateFlow = MutableStateFlow<String>("")
    override fun init(scope: CoroutineScope) {
        // Do nothing
    }

    override fun onDataReady(scope: CoroutineScope, data: String) {
        _uiStateFlow.value = "$data: FeatureOne"
    }

    override val uiStateFlow: StateFlow<String> = _uiStateFlow

    override fun onEvent(event: Any) {
        // Do nothing
    }
}