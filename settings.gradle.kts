pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "koreRoot"
include(":kore")
include(":spring")