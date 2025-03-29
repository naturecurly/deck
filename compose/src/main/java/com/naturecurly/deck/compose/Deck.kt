package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.naturecurly.deck.DeckProvider
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun Deck(provider: DeckProvider<*>, content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(
        provider.containerUis.filterValues { it is DeckComposeContainerUi<*, *> }
            .mapValues { it.value as DeckComposeContainerUi }.toImmutableMap(),
    ).apply { content() }
}

@Composable
private fun rememberDeckScope(containers: ImmutableMap<String, DeckComposeContainerUi<*, *>>) =
    remember(containers) {
        DeckScopeImpl(containers)
    }

@Composable
fun DeckPreview(content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope(persistentMapOf()).apply {
        content()
    }
}
