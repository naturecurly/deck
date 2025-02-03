package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

@Immutable
abstract class DeckScope {
    protected abstract val containers: Map<String, DeckComposeContainer<*, *>>

    @Composable
    fun Stub(containerId: String, modifier: Modifier = Modifier) {
        containers[containerId]?.Content(modifier)
    }
}
