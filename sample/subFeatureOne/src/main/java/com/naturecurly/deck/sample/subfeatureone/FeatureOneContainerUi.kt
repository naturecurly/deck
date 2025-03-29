package com.naturecurly.deck.sample.subfeatureone

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturecurly.deck.annotations.ContainerUi
import com.naturecurly.deck.compose.DeckComposeContainerUi
import javax.inject.Inject

@ContainerUi(bindTo = "MainFeature")
class FeatureOneContainerUi @Inject constructor() : DeckComposeContainerUi<String, FeatureOneDeckContainer>() {
    override val id: String
        get() = "FeatureOne"

    @Composable
    override fun Content(modifier: Modifier) {
        val uiState by container.uiStateFlow.collectAsStateWithLifecycle()
        Text(
            text = uiState,
            modifier = modifier.clickable {
                container.onEvent(Event.UpdateValueEvent)
            },
        )
    }
}
