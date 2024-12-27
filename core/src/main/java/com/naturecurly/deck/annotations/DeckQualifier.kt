package com.naturecurly.deck.annotations

import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeckQualifier(val providerId: String)
