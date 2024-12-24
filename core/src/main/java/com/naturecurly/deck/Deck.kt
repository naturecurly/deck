package com.naturecurly.deck

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun Deck(content: @Composable DeckScope.() -> Unit) {
    rememberDeckScope().apply { content() }
}

@Composable
private fun rememberDeckScope() = remember {
    DeckScopeImpl()
}