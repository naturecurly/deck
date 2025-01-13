package com.naturecurly.deck.compose

import android.content.Context
import androidx.startup.Initializer

class DeckInitializer : Initializer<WharfImpl> {
    override fun create(context: Context): WharfImpl =
        WharfImpl().apply {
            init(context)
        }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}
