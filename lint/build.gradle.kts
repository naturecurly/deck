plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.lint.api)
    compileOnly(libs.lint.check)
}

tasks.jar {
    manifest { attributes("Lint-Registry-v2" to "com.naturecurly.deck.lint.CheckRegistry") }
}
