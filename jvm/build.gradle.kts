import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
}
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
repositories {
    mavenCentral()
}
dependencies {
    implementation(project(mapOf("path" to ":kore")))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    testImplementation(kotlin("test"))
}
tasks.withType<KotlinCompile>{
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}
tasks.withType<Test>{
    useJUnitPlatform()
}