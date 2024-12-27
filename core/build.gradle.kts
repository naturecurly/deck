plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jakarta.inject)
    implementation(libs.hilt.core)
}