package com.naturecurly.deck.sample.subfeatureone

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.compose.DeckComposeContainer
import javax.inject.Inject

@Container(consumerClass = FeatureOneDeckConsumer::class)
class FeatureOneContainer @Inject constructor(): DeckComposeContainer<String, FeatureOneDeckConsumer>() {
    override val id: String
        get() = "FeatureOne"

    @Composable
    override fun Content() {
        val uiState by consumer.uiStateFlow.collectAsStateWithLifecycle()
        Text(text = uiState)
    }
}