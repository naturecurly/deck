package com.naturecurly.deck.compose

import android.content.Context
import androidx.startup.Initializer

class DeckInitializer : Initializer<WharfProxy> {
    override fun create(context: Context): WharfProxy =
        WharfProxy.apply {
            init(context)
        }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}