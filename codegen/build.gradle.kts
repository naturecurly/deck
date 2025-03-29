plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.hilt.core)
    implementation(libs.jakarta.inject)
    compileOnly(libs.ksp)
    compileOnly(libs.ksp.api)
}
