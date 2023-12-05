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
    @Suppress("OPT_IN_USAGE")
    wasmJs {
        binaries.executable()
        browser {

        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val wasmJsMain by getting
        val wasmJsTest by getting
    }
//    sourceSets.all{
//        languageSettings.languageVersion = "2.0"
//    }
}