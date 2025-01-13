package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.naturecurly.deck.DeckProvider
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun Deck(provider: DeckProvider<*>, content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(provider.containers.filterValues { it is DeckComposeContainer<*, *> }
        .mapValues { it.value as DeckComposeContainer }.toImmutableMap()).apply { content() }
}

@Composable
private fun rememberDeckScope(containers: ImmutableMap<String, DeckComposeContainer<*, *>>) =
    remember(containers) {
        DeckScopeImpl(containers)
    }

@Composable
fun DeckPreview(content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(persistentMapOf()).apply {
        content()
    }
}