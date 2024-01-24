import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform")
}
repositories {
    mavenCentral()
}
tasks.withType<KotlinJsCompile>().configureEach {
    kotlinOptions{
        useEsClasses = true
        typedArrays = true
        sourceMap = false
    }
}
kotlin {
    js{
        moduleName = "kore"
        useEsModules()
        browser {
            webpackTask{
                sourceMaps = false
                mainOutputFileName = "kore.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(mapOf("path" to ":kore")))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jsMain by getting{
            dependencies {
            }
        }
        val jsTest by getting
    }
}
