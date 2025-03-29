package com.naturecurly.deck

abstract class DeckContainerUi<INPUT, CONTAINER : DeckContainer<*, INPUT>> {
    abstract val id: String
    protected lateinit var container: CONTAINER
        private set

    internal fun setContainer(container: DeckContainer<*, *>) {
        @Suppress("UNCHECKED_CAST")
        this.container = container as CONTAINER
    }
}
