plugins {
    id("dev.architectury.loom")
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
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}") {
        exclude(module = "fabric-content-registries-v0")
    }

    // Architectury API removed!

    // Cloth Config
    modApi("me.shedaniel.cloth:cloth-config-fabric:${project.property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    // ModMenu
    modApi("com.terraformersmc:modmenu:${project.property("modmenu_version")}")

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionFabric")) { isTransitive = false }
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
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)

        archiveBaseName.set("[${project.property("minecraft_version")}] ${project.property("mod_id")}")
        archiveVersion.set("v${project.property("mod_version")}")
        archiveClassifier.set("fabric")

        doLast {
            archiveFile.orNull?.asFile?.let { jarFile ->
                if (jarFile.exists()) {
                    val targetDir = rootProject.file("local/builds")
                    targetDir.mkdirs()
                    jarFile.copyTo(File(targetDir, jarFile.name), overwrite = true)
                }
            }
        }
    }
}