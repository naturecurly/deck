package com.naturecurly.deck

import android.content.Context
import androidx.startup.Initializer

class DeckInitializer : Initializer<Wharf> {
    override fun create(context: Context): Wharf =
        Wharf.apply {
            init(context)
        }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}