package com.naturecurly.deck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
interface DeckScope {
    val containers: Map<String, DeckContainer<*, *>>

    @Composable
    fun Stub(containerId: String) {
        containers[containerId]?.Content()
    }
}