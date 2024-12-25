package com.naturecurly.deck

import androidx.compose.runtime.Composable


abstract class DeckContainer<INPUT, CONSUMER : DeckConsumer<*, INPUT>> {
    abstract val id: String
    lateinit var consumer: CONSUMER
        private set

    @Composable
    abstract fun Content()

    internal fun setConsumer(consumer: DeckConsumer<*, *>) {
        @Suppress("UNCHECKED_CAST")
        this.consumer = consumer as CONSUMER
    }
}