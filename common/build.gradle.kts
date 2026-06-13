plugins {
    id("dev.architectury.loom")
}

architectury {
    common(project.property("enabled_platforms").toString().split(","))
}

loom {
    accessWidenerPath = file("src/main/resources/undertale_death_screen.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other fabric deps here
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}")
    
    // Removed Architectury API to avoid runtime dependency
    // Cloth Config
    modApi("me.shedaniel.cloth:cloth-config-fabric:${project.property("cloth_config_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}
