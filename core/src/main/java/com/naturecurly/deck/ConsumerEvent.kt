package com.naturecurly.deck

sealed interface ConsumerEvent {
    data object RefreshProvider : ConsumerEvent
    data class CustomEvent<T>(val key: String, val value: T) : ConsumerEvent
}
