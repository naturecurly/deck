package com.naturecurly.deck.sample.subfeatureone

import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.sample.subfeatureone.Event.UpdateValueEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Container(bindTo = "MainFeature")
class FeatureOneDeckContainer @Inject constructor() : DeckContainer<String, String>() {
    private val _uiStateFlow = MutableStateFlow<String>("")
    lateinit var scope: CoroutineScope
    override fun init(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onDataReady(scope: CoroutineScope, data: String) {
        _uiStateFlow.value = "$data: FeatureOne"
    }

    override val uiStateFlow: StateFlow<String> = _uiStateFlow

    override fun <Event> onEvent(event: Event) {
        when (event) {
            UpdateValueEvent -> {
                _uiStateFlow.value = "Updated FeatureOne"
            }
        }
    }
}

internal sealed interface Event {
    data object UpdateValueEvent : Event
}
