package com.naturecurly.deck.sample.subfeaturetwo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturecurly.deck.annotations.ContainerUi
import com.naturecurly.deck.compose.DeckComposeContainerUi
import com.naturecurly.deck.sample.subfeaturetwo.model.FeatureTwoModel
import javax.inject.Inject

@ContainerUi(bindTo = "MainFeature")
class FeatureTwoContainerUi @Inject constructor() : DeckComposeContainerUi<FeatureTwoModel, FeatureTwoContainer>() {
    @Composable
    override fun Content(modifier: Modifier) {
        val state by container.uiStateFlow.collectAsStateWithLifecycle()
        Column(modifier) {
            Text(state.title)
            Text(state.subtitle)
        }
    }

    override val id: String = "FeatureTwo"
}
