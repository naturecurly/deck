package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.naturecurly.deck.DeckConsumer
import com.naturecurly.deck.DeckContainer

abstract class DeckComposeContainer<INPUT, CONSUMER : DeckConsumer<*, INPUT>> : DeckContainer<INPUT, CONSUMER>() {

    @Composable
    abstract fun Content(modifier: Modifier)
}
