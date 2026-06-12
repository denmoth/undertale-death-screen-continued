pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.9"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        versions("1.20", "1.20.2", "1.20.6", "1.21.3", "1.21.5", "1.21.6", "1.21.8", "1.21.9", "1.21.11", "26.1", "26.1.2")
        vcsVersion = "1.20.6"
        branch("fabric")
        branch("forge") { versions("1.20", "1.20.2", "1.20.6") }
        branch("neoforge") { versions("1.20.2", "1.20.6", "1.21.3", "1.21.5", "1.21.6", "1.21.8", "1.21.9", "1.21.11", "26.1", "26.1.2") }
    }
}

rootProject.name = "Undertale Death Screen"