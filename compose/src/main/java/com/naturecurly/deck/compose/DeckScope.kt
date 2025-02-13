package com.naturecurly.deck.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.naturecurly.deck.compose.log.DeckLog

@Immutable
abstract class DeckScope {
    protected abstract val containers: Map<String, DeckComposeContainer<*, *>>

    @SuppressLint("RememberReturnType")
    @Composable
    fun Stub(containerId: String, modifier: Modifier = Modifier) {
        containers[containerId]?.Content(modifier) ?: remember(containerId) {
            DeckLog.w("Not found containerId: $containerId")
        }
    }
}
