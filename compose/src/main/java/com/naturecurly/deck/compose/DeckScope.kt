package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
interface DeckScope {
    val containers: Map<String, DeckComposeContainer<*, *>>

    @Composable
    fun Stub(containerId: String) {
        containers[containerId]?.Content()
    }
}