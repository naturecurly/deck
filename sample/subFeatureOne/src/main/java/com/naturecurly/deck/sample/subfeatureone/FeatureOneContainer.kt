package com.naturecurly.deck.sample.subfeatureone

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.compose.DeckComposeContainer
import javax.inject.Inject

@Container
class FeatureOneContainer @Inject constructor() :
    DeckComposeContainer<String, FeatureOneDeckConsumer>() {
    override val id: String
        get() = "FeatureOne"

    @Composable
    override fun Content() {
        val uiState by consumer.uiStateFlow.collectAsStateWithLifecycle()
        Text(text = uiState, modifier = Modifier.clickable {
            consumer.onEvent(Event.UpdateValueEvent)
        })
    }
}