package com.naturecurly.deck.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Consumer(val bindTo: String)