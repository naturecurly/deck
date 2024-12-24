package com.naturecurly.deck.sample.subfeatureone

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.naturecurly.deck.DeckContainer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.naturecurly.deck.annotations.Container
import javax.inject.Inject

@Container(consumerClass = FeatureOneDeckConsumer::class)
class FeatureOneContainer @Inject constructor(): DeckContainer<String, FeatureOneDeckConsumer>() {
    override val id: String
        get() = "FeatureOne"

    @Composable
    override fun Content() {
        val uiState by consumer.uiStateFlow.collectAsStateWithLifecycle()
        Text(text = uiState)
    }
}