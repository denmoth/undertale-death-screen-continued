plugins {
    id("dev.architectury.loom-no-remap") version "1.14-SNAPSHOT" apply false
    id("architectury-plugin") version "3.5-SNAPSHOT"
    id("io.github.goooler.shadow") version "8.1.8" apply false
}

architectury {
    minecraft = project.property("minecraft_version").toString()
}



allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    project.extensions.getByType<org.gradle.api.plugins.BasePluginExtension>().archivesName.set(project.property("mod_id").toString())
    version = project.property("mod_version").toString()
    group = project.property("maven_group").toString()

    repositories {
        maven("https://maven.shedaniel.me/") // Cloth Config, Architectury
        maven("https://maven.terraformersmc.com/") // ModMenu
        maven("https://maven.parchmentmc.org") // Parchment
        maven("https://maven.neoforged.net/releases/")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(25)
    }

    configure<org.gradle.api.plugins.JavaPluginExtension> {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }
}