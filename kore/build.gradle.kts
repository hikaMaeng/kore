import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
}
repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
}
kotlin {
    jvm{
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
                freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
            }
        }
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }
    js{
        browser()
        nodejs()
    }
    @Suppress("OPT_IN_USAGE")
    wasmJs()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
            }
        }
        val jvmMain by getting
        val wasmJsMain by getting
        val jsMain by getting
    }
//    sourceSets.all{
//        languageSettings.languageVersion = "2.0"
//    }
}