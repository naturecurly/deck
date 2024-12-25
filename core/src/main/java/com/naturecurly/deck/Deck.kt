package com.naturecurly.deck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun Deck(provider: DeckProvider<*>, content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(provider.containers.toImmutableMap()).apply { content() }
}

@Composable
private fun rememberDeckScope(containers: ImmutableMap<String, DeckContainer<*, *>>) =
    remember(containers) {
        DeckScopeImpl(containers)
    }