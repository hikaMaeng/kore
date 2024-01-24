
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
//    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
}
//compose.experimental {
//    web.application {}
//}
kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "ein.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(mapOf("path" to ":kore")))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
//                implementation(compose.runtime)
//                implementation(compose.foundation)
//                implementation(compose.material)
//                implementation(compose.ui)
//                @OptIn(ExperimentalComposeLibrary::class)
//                implementation(compose.components.resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val wasmJsMain by getting{
            dependencies {
            }
        }
        val wasmJsTest by getting
    }
//    sourceSets.all{
//        languageSettings.languageVersion = "2.0"
//    }
}