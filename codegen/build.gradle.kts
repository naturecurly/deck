plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlinpoet.ksp)
    compileOnly(libs.ksp)
    compileOnly(libs.ksp.api)
}