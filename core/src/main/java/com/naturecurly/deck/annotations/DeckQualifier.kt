package com.naturecurly.deck.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeckQualifier(val providerId: String)
