package com.naturecurly.deck.sample.subfeaturetwo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturecurly.deck.annotations.Container
import com.naturecurly.deck.compose.DeckComposeContainer
import com.naturecurly.deck.sample.subfeaturetwo.model.FeatureTwoModel
import javax.inject.Inject

@Container(bindTo = "MainFeature")
class FeatureTwoContainer @Inject constructor() : DeckComposeContainer<FeatureTwoModel, FeatureTwoConsumer>() {
    @Composable
    override fun Content(modifier: Modifier) {
        val state by consumer.uiStateFlow.collectAsStateWithLifecycle()
        Column(modifier) {
            Text(state.title)
            Text(state.subtitle)
        }
    }

    override val id: String = "FeatureTwo"
}
