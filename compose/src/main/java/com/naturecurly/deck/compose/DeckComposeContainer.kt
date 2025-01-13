package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer

abstract class DeckComposeContainer<INPUT, CONSUMER : DeckConsumer<*, INPUT>> : DeckContainer<INPUT, CONSUMER>() {

    @Composable
    abstract fun Content()
}
