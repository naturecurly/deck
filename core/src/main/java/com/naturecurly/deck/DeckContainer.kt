package com.naturecurly.deck

abstract class DeckContainer<INPUT, CONSUMER : DeckConsumer<*, INPUT>> {
    abstract val id: String
    lateinit var consumer: CONSUMER
        private set

    fun setConsumer(consumer: DeckConsumer<*, *>) {
        @Suppress("UNCHECKED_CAST")
        this.consumer = consumer as CONSUMER
    }
}