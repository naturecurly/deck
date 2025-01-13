plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    alias(libs.plugins.mavenPublish)
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.hilt.core)
    implementation(libs.jakarta.inject)
    compileOnly(libs.ksp)
    compileOnly(libs.ksp.api)
}