package com.naturecurly.deck.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ContainerUi(val bindTo: String)
