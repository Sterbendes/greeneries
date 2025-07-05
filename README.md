# Gradle Multi-loader mod template (fabric/neoforge)

### An easy-to-use template for creating fabric and neoforge mods using gradle for Minecraft 1.17 to 1.21.1+

### Features:
😎 **Easy to use** (see the [Getting started](#getting-started) section)<br>
🚀 **Blazingly fast** thanks to gradle configuration cache*<br>
📖 **Well-documented** (see the [More information](#more-information) section below)<br>
🔋 **Batteries included** with working run configurations as well as GitHub actions enabled by default <br> 
<img style="height: 1rem" alt="crossed out copyright icon" src="https://upload.wikimedia.org/wikipedia/commons/e/eb/PD-icon-black.svg"/>
**Free of copyright restrictions** as it is subject to the public domain by using [Unlicense](https://unlicense.org/)<br>

> *Importing and building the project for the first time might take quite a while, but
> once that's done building or running the project will be pretty fast!

## Getting started
0. **Create your repo** <br>
   Click the "Use this template" button in the top right to create a repository, then clone & import your new repository into IntelliJ IDEA (or your preferred IDE)
1. **Add mod info** <br>
   Open the [`settings.gradle.kts`](settings.gradle.kts) file and put your mod id as the project name (at the very top of the file).
   Then, check the [`gradle.properties`](gradle.properties) file and set your mod info like id, package, name and authors there.
2. **IDE integration** <br>
   First, import your repo into IDEA and import the gradle project if it isn't importing already (this might take a while).
   Once done, you can run the `genSources` gradle task in the fabric category and then use the "download sources" button once that's finished to
   ensure you have access to the Minecraft source code in all modules.
4. **Rename package** <br>
   In the `common`, `fabric` and `neoforge` modules, refactor the package name from `net.yourpackage.yourmod` to your actual package name. Also adjust the java file names and the `modID` field
5. **Done** <br>
   You can now enjoy modding in a multi-loader setup!
   Working run configurations are automatically generated - just select the relevant one in the top right of your IDE window and run the game right from your IDE.

## More information
<details style="margin-bottom: 5px">
   <summary><b>Project structure</b></summary>
   
   A multi-loader project consists of a root gradle project and three subprojects: `common`, `fabric` and `neoforge`. <br>
   The root project should not contain any code. It's build.gradle.kts file is used for some  common configuration for all the subprojects. <br>
   The `common` subproject contains all the **common mod code**, which will be included in all built jars. It has access to all of Minecraft,
   and the ability to add Access wideners and mixins, but no access to any mod loader's API.
   It's build.gradle.kts is the place to put most of your required dependencies. <br>
   The `fabric` and `neoforge` subprojects contain initialisation and **loader-specific code**, as well as loader-specific resources like `fabric.mod.json` and `neoforge.mods.toml` <br>
   
</details>

<details style="margin-bottom: 5px">
   <summary><b>Switching Minecraft versions</b></summary>

   In order to change the target Minecraft version, just set the `minecraft_version` property in the [`gradle.properties`](gradle.properties)
   file and adapt the other properties
   (`mc_versions_fabric`, `mc_versions_neo`, `parchment_version`, `neoforge_version`, `fabric_loader_version` and `fabric_api_version`) appropriately.

   This, however, only works for versions later than 1.20.1, because the neoforge ModDevGradle plugin only works for these newer versions.
   Therefore, this template provides a `1.20.1` branch that you can use instead if you want to use Minecraft versions from 1.17 to 1.20.1.

</details>

<details style="margin-bottom: 5px">
   <summary><b>Access wideners/Access transformers</b></summary>

   To use access wideners, create a `.accesswidener` file somewhere in your common resources directory and define the path in the `common/build.gradle.kts` file (~ line 30).
   These access wideners will be loaded in `common` and `fabric`, but they won't work on neoforge. That means you will need to create an `accesstransfomer.cfg` file inside
   your `neoforge/resources/META-INF` and add the same entries to that. The access transformer file will be loaded automatically if it has the default location & file name.

   [More information on access wideners](https://wiki.fabricmc.net/tutorial:accesswideners) / [More information on access transformers](https://docs.neoforged.net/docs/advanced/accesstransformers/#the-access-transformer-specification)

</details>

<details style="margin-bottom: 5px">
   <summary><b>Mixins</b></summary>

   To use mixins in your project, just create a mixin configuration file (`mymod.mixins.json`) in your common resources and add the path of it to your
   [`fabric.mod.json`](fabric/src/main/resources/fabric.mod.json) and [`neoforge.mods.toml`](neoforge/src/main/resources/META-INF/neoforge.mods.toml) files. No additional configuration required.

   More about mixins: [Mixin introduction](https://wiki.fabricmc.net/tutorial:mixin_introduction) / [Mixin examples](https://wiki.fabricmc.net/tutorial:mixin_examples)

</details>

<details style="margin-bottom: 5px">
   <summary><b>Platform-specific conversions</b></summary>

   This template can automatically convert some platform-specific
   parts of datapacks such as fluid units and data loading conditions
   from a defined common format to the platform-specific formats.

   See [platform_conversions.md](./platform_conversions.md) for more information.

</details>

<details>
   <summary><b>Automatic Release Publishing</b></summary>

   This template uses [mc-publish](https://github.com/Kir-Antipov/mc-publish)
   to allow you to automatically publish your mod to Modrinth, Curseforge and GitHub releases.

   In order to set this up, add the project IDs of your modrinth and curseforge projects
   in the [`release.yml`](.github/workflows/release.yml) action file.

   Then, open your GitHub repository settings, select "Secrets and Variables" and then "Actions"
   in the left sidebar.
   Next up, [generate a Modrinth personal access token](https://modrinth.com/settings/pats)
   and use the green "New Repository Secret" button in GitHub to add the token with the name "MODRINTH_TOKEN".
   Repeat the process by [generating a curseforge API token](https://legacy.curseforge.com/account/api-tokens) 
   and adding it as a secret named "CURSEFORGE_TOKEN".

   Then, you can publish your mod to Modrinth and curseforge by creating a GitHub release with no attached files.
   Your mod will be built to a jar file automatically, which will then be uploaded to Modrinth and curseforge and
   also attached to the GitHub release.

</details>

### Have questions or need help?
Please [contact me on Discord (@player.005)](https://discord.com/users/650714531844194304) or [open an issue on GitHub](https://github.com/Player005/multiloader-mod-template/issues/new).
