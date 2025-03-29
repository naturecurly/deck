package com.naturecurly.deck.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.naturecurly.deck.DeckContainer
import com.naturecurly.deck.DeckContainerUi

abstract class DeckComposeContainerUi<INPUT, CONTAINER : DeckContainer<*, INPUT>> : DeckContainerUi<INPUT, CONTAINER>() {

    @Composable
    abstract fun Content(modifier: Modifier)
}
