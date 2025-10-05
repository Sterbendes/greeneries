@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
}

// add a repositories block here for fabric-only dependencies if you need it

dependencies.project(":common")

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.properties["minecraft_version"]}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${rootProject.properties["parchment_version"]}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${rootProject.properties["fabric_loader_version"]}")
    // This line can be removed if you don't need fabric api
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.properties["fabric_api_version"]}")

    implementation(project.project(":common").sourceSets.getByName("main").output)

    // Add fabric-only dependencies here.
}

loom {
    runs {
        val vmArgs = arrayOf("-XX:+UseZGC", "-XX:+ZGenerational", "-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-Xms500M", "-Xmx2G")
        named("client") {
            client()
            runDir("../run/client/${properties["minecraft_version"]}")
            configName = "Fabric/Client"
            vmArgs(*vmArgs)
        }
        named("server") {
            server()
            runDir("../run/server/${properties["minecraft_version"]}")
            configName = "Fabric/Server"
            vmArgs(*vmArgs)
        }
    }

    // include access wideners from common
    accessWidenerPath = project(":common").loom.accessWidenerPath
}

tasks {
    withType<JavaCompile> {
        // include common code in compiled jar
        source(project(":common").sourceSets.main.get().allSource)
    }

    // put all artifacts in the right directory
    withType<Jar> {
        destinationDirectory = rootDir.resolve("build").resolve("libs_fabric")
    }
    withType<RemapJarTask> {
        destinationDirectory = rootDir.resolve("build").resolve("libs_fabric")
    }

    // add common javadoc to jar
    javadoc { source(project(":common").sourceSets.main.get().allJava) }

    processResources {
        // add common resources to jar
        from(project(":common").sourceSets.main.get().resources)
        from(project(":common").sourceSets["generated"].resources)

        // the properties listed here can be used in the fabric.mod.json
        val properties =
            listOf(
                "mc_versions_fabric", "mod_version", "mod_id", "mod_name",
                "mod_description", "mod_authors", "mod_license"
            )

        val map = mutableMapOf<String, String>()
        properties.forEach { map[it] = rootProject.properties[it].toString() }
        inputs.property("property_map", map)

        filesMatching("fabric.mod.json") {
            @Suppress("UNCHECKED_CAST")
            expand(inputs.properties["property_map"] as Map<String, String>)
        }

        // do fluid unit conversions and unified load condition processing if enabled
        // see `Platform conversions.md` for more information
        inputs.property("handle_fluid_unit_conversion", rootProject.properties["handle_fluid_unit_conversion"] == "true")
        inputs.property("unified_load_conditions", rootProject.properties["unified_load_conditions"] == "true")

        filesMatching("data/**/*.json") {
            if (inputs.properties["handle_fluid_unit_conversion"] as Boolean)
                filter(FabricConversions.fluidUnitConverter)
            if (inputs.properties["unified_load_conditions"] as Boolean)
                filter(FabricConversions.unifiedLoadConditionProcessor)
        }
    }

    named("compileTestJava").configure {
        enabled = false
    }

    named("test").configure {
        enabled = false
    }
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object FabricConversions {

    val fluidUnitConverter = Transformer<String?, String> { line ->
        var result = line.replace(""""(\d*\.?\d*)_droplets"""".toRegex(), "$1")
        val regex = """"(\d*\.?\d*)_millibuckets"""".toRegex()
        regex.find(line)?.let { result = result.replace(regex, (it.groups[1]!!.value.toInt() * 81).toString()) }
        result
    }

    val unifiedLoadConditionProcessor = Transformer<String?, String> { line ->
        // I am very sorry for anyone who tries to read or understand this
        line.replace(
            """^(\s*)"load_conditions":""".toRegex(),
            "$1\"fabric:load_conditions\":"
        )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"mod_loaded"""".toRegex(),
                "$1\"condition\": \"fabric:all_mods_loaded\""
            )
            .replace(
                """^(\s*)"modid":\s*"(.*)"""".toRegex(),
                "$1\"values\": [\"$2\"]"
            )
            .replace(
                """^(\s*)"not":\s*\{""".toRegex(),
                "$1\"condition\": \"fabric:not\",\n$1\"value\": {"
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"and"""".toRegex(),
                "$1\"condition\": \"fabric:and\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"or"""".toRegex(),
                "$1\"condition\": \"fabric:or\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"is_fabric"""".toRegex(),
                "$1\"condition\": \"true\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"is_neoforge"""".toRegex(),
                "$1\"condition\": \"false\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"true"""".toRegex(),
                "$1\"condition\": \"fabric:true\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"false"""".toRegex(),
                "$1\"condition\": \"fabric:not\", \"value\": {\"condition\": \"fabric:true\"}"
            )
    }
}
