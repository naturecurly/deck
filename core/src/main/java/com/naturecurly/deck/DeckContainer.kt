package com.naturecurly.deck

import androidx.compose.runtime.Composable


abstract class DeckContainer<INPUT, CONSUMER : DeckConsumer<*, INPUT>> {
    abstract val id: String
    val consumer: CONSUMER by lazy { Wharf.getDeckConsumer(this::class) }

    @Composable
    abstract fun Content()
}