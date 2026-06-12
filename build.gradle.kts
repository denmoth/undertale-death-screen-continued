plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")

    id("dev.kikugie.fletching-table")
}

val minecraft = stonecutter.current.version

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.project.prop("loom.platform")
})

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.shedaniel.me/")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    modCompileOnly("me.shedaniel.cloth:cloth-config-fabric:${mod.dep("cloth_config")}") {
        exclude(group = "net.fabricmc")
    }
}

java {
    val javaVer = when {
        minecraft.startsWith("1.20.4") -> 17
        minecraft.startsWith("1.20") -> 21
        minecraft.startsWith("1.21") -> 21
        minecraft.startsWith("26.") -> 25
        else -> 21
    }
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVer))
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")
}