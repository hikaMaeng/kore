import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
}
compose.experimental {
    web.application {}
}
kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(mapOf("path" to ":kore")))
                //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
//                implementation("org.jetbrains.kotlinx:atomicfu-wasm-js:0.22.0-wasm2")
            }
        }
        val wasmJsMain by getting{
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-wasm:1.7.2-wasm0")
//                implementation("org.jetbrains.kotlinx:atomicfu-wasm-js:0.22.0-wasm2")
            }
        }
        val wasmJsTest by getting
    }
//    sourceSets.all{
//        languageSettings.languageVersion = "2.0"
//    }
}