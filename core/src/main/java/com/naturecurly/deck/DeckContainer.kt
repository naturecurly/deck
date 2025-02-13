package com.naturecurly.deck

abstract class DeckContainer<INPUT, CONSUMER : DeckConsumer<*, INPUT>> {
    abstract val id: String
    protected lateinit var consumer: CONSUMER
        private set

    internal fun setConsumer(consumer: DeckConsumer<*, *>) {
        @Suppress("UNCHECKED_CAST")
        this.consumer = consumer as CONSUMER
    }
}
