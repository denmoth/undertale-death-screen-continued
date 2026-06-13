import org.gradle.internal.jvm.Jvm
import org.gradle.api.JavaVersion

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

val isJava25 = Jvm.current().javaVersion?.isCompatibleWith(JavaVersion.VERSION_25) == true

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        val allVersions = mutableListOf("1.20", "1.20.2", "1.20.6", "1.21.3", "1.21.5", "1.21.6", "1.21.8", "1.21.9")
        val neoforgeVersions = mutableListOf("1.20.2", "1.20.6", "1.21.3", "1.21.5", "1.21.6", "1.21.8", "1.21.9")
        if (isJava25) {
            allVersions.addAll(listOf("26.1", "26.1.2"))
            neoforgeVersions.addAll(listOf("26.1", "26.1.2"))
        } else {
            logger.warn("WARNING: You are using Java version < 25. Minecraft 26.x requires Java 25. The 26.x versions will be skipped during this build!")
        }

        versions(*allVersions.toTypedArray())
        vcsVersion = "1.20.6"
        branch("fabric")
        branch("forge") { versions("1.20", "1.20.2", "1.20.6") }
        branch("neoforge") { versions(*neoforgeVersions.toTypedArray()) }
    }
}

rootProject.name = "Undertale Death Screen"