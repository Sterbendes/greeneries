plugins {
    id("net.neoforged.moddev") version "2.0.107"
}

// put a repositories block here for neoforge-only repositories if you need it

dependencies {
    implementation(project.project(":common").sourceSets.getByName("main").output)

    // Add neoforge-only dependencies here.
}

neoForge {
    version = rootProject.properties["neoforge_version"].toString()

    parchment {
        minecraftVersion = rootProject.properties["parchment_version"].toString().split(":").first()
        mappingsVersion = rootProject.properties["parchment_version"].toString().split(":").last()
    }

    runs {
        val vmArgs = arrayOf("-XX:+UseZGC", "-XX:+ZGenerational", "-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-Xms500M", "-Xmx2G")
        create("Client") {
            client()
            gameDirectory = rootProject.file("run/client/${rootProject.properties["minecraft_version"]}")
            jvmArguments.addAll(*vmArgs)
        }
        create("Server") {
            server()
            gameDirectory = rootProject.file("run/server/${rootProject.properties["minecraft_version"]}")
            jvmArguments.addAll(*vmArgs)
        }
        create("Data") {
            serverData()
            gameDirectory = rootProject.file("run/data/${rootProject.properties["minecraft_version"]}")
            jvmArguments.addAll(*vmArgs)
            programArguments.addAll("--mod", "greeneries",/* "--includeClient", "--includeServer", */"--output",
                rootProject.file("common/src/generated/resources").absolutePath)
        }
    }

    mods {
        create(rootProject.properties["mod_id"].toString()) {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks {
    jar {
        // add common code to jar
        val main = project.project(":common").sourceSets.main.get()
        from(main.output.classesDirs)
        from(main.output.resourcesDir)
    }

    getByName("compileTestJava") {
        enabled = false
    }

    // NeoGradle compiles the game, but we don't want to add our common code to the game's code
    val notNeoTask: (Task) -> Boolean = { !it.name.startsWith("neo") && !it.name.startsWith("compileService") }

    // add common code & javadoc to built jars (except for NeoGradle jars)
    withType<JavaCompile>().matching(notNeoTask).configureEach {
        source(project(":common").sourceSets.main.get().allSource)
    }
    withType<Javadoc>().matching(notNeoTask).configureEach {
        source(project(":common").sourceSets.main.get().allSource)
    }

    withType<ProcessResources>().matching(notNeoTask).configureEach {
        // include common resources
        from(project(":common").sourceSets.main.get().resources)
        from(project(":common").sourceSets["generated"].resources)

        // the properties listed here can be used in the mods.toml
        val properties =
            listOf(
                "mc_versions_neo", "neo_loader_version_range", "mod_version", "mod_id", "mod_name",
                "mod_description", "mod_authors", "mod_license"
            )

        // store a map of the properties so the configuration cache can be used
        val map = mutableMapOf<String, String>()
        properties.forEach { map[it] = rootProject.properties[it].toString() }
        inputs.property("property_map", map)

        filesMatching("META-INF/neoforge.mods.toml") {
            @Suppress("UNCHECKED_CAST")
            expand(inputs.properties["property_map"] as Map<String, String>)
        }

        // do fluid unit conversions and unified load condition processing if enabled
        // see `Platform conversions.md` for more information
        inputs.property("handle_fluid_unit_conversion", rootProject.properties["handle_fluid_unit_conversion"] == "true")
        inputs.property("unified_load_conditions", rootProject.properties["unified_load_conditions"] == "true")

        filesMatching("data/**/*.json") {
            if (inputs.properties["handle_fluid_unit_conversion"] as Boolean)
                filter(NeoforgeConversions.fluidUnitConverter)
            if (inputs.properties["unified_load_conditions"] as Boolean)
                filter(NeoforgeConversions.unifiedLoadConditionProcessor)
        }
    }
}


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object NeoforgeConversions {

    val fluidUnitConverter = Transformer<String?, String> { line ->
        var result = line.replace(""""(\d*\.?\d*)_millibuckets"""".toRegex(), "$1")
        val regex = """"(\d*\.?\d*)_droplets"""".toRegex()
        regex.find(line)?.let { result = result.replace(regex, (it.groups[1]!!.value.toInt() / 81).toString()) }
        result
    }

    val unifiedLoadConditionProcessor = Transformer<String?, String> { line ->
        // I am very sorry for anyone who tries to read or understand this
        line.replace(
            """^(\s*)"load_conditions":""".toRegex(),
            "$1\"neoforge:conditions\":"
        )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"mod_loaded"""".toRegex(),
                "$1\"type\": \"neoforge:mod_loaded\""
            )
            .replace(
                """^(\s*)"not":\s*\{""".toRegex(),
                "$1\"type\": \"neoforge:not\",\n$1\"value\": {"
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"and"""".toRegex(),
                "$1\"type\": \"neoforge:and\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"or"""".toRegex(),
                "$1\"type\": \"neoforge:or\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"is_fabric"""".toRegex(),
                "$1\"type\": \"neoforge:false\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"is_neoforge"""".toRegex(),
                "$1\"type\": \"neoforge:true\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"true"""".toRegex(),
                "$1\"type\": \"neoforge:true\""
            )
            .replace(
                """^(\s*\{?\s*)"condition":\s*"false"""".toRegex(),
                "$1\"type\": \"neoforge:false\""
            )
    }
}
