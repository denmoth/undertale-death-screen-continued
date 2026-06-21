plugins {
    id("dev.architectury.loom-no-remap")
    id("io.github.goooler.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentFabric").extendsFrom(configurations["common"])
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    // mappings(loom.officialMojangMappings())

    implementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}")
    api("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")

    // Architectury API removed!

    // Cloth Config
    api("me.shedaniel.cloth:cloth-config-fabric:${project.property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    // ModMenu
    api("com.terraformersmc:modmenu:${project.property("modmenu_version")}")

    common(project(":common")) { isTransitive = false }
    shadowCommon(project(":common")) { isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("architectury.common.json")
        configurations = listOf(shadowCommon)
        archiveClassifier.set("fabric")
    }
}