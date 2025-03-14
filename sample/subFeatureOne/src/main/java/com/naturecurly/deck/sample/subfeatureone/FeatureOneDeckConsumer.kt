package com.naturecurly.deck.sample.subfeatureone

import com.naturecurly.deck.ConsumerEvent
import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.annotations.Consumer
import com.naturecurly.deck.sample.subfeatureone.Event.UpdateValueEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Consumer(bindTo = "MainFeature")
class FeatureOneDeckConsumer @Inject constructor() : DeckConsumer<String, String>() {
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
                notifyProvider(ConsumerEvent.CustomEvent<String>("custom", "refresh"), scope)
            }
        }
    }
}

internal sealed interface Event {
    data object UpdateValueEvent : Event
}
