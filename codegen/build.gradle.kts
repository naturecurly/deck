plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlinpoet.ksp)
    compileOnly(libs.ksp)
    compileOnly(libs.ksp.api)
}