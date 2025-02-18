package com.naturecurly.deck

internal object WharfLocal {
    private lateinit var wharf: Wharf
    internal fun init(wharf: Wharf) {
        this.wharf = wharf
    }

    internal fun get() = wharf
}
