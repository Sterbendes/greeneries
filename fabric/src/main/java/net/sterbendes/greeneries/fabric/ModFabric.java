package net.sterbendes.greeneries.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.sterbendes.greeneries.GreeneriesMod;
import net.sterbendes.greeneries.GreeneriesPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GreeneriesMod.init(new GreeneriesFabricPlatform());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, rm, success) -> {
            if (success) doBiomeModifications(server);
        });
    }

    private static Map<TagKey<Biome>, TagKey<PlacedFeature>> getAddedFeaturesMap() {
        var strings = Map.of(
            "c:is_hot/overworld", "greeneries:patches_hot",
            "c:is_jungle", "greeneries:patches_jungles",
            "c:is_temperate/overworld", "greeneries:patches_temperate",
            "c:is_cold/overworld", "greeneries:patches_cold",
            "greeneries:has_reeds", "greeneries:patches_water",
            "c:is_wet/overworld", "greeneries:patches_wet"
        );

        var map = new HashMap<TagKey<Biome>, TagKey<PlacedFeature>>();
        strings.forEach((str, str2) -> map.put(
            TagKey.create(Registries.BIOME, ResourceLocation.parse(str)),
            TagKey.create(Registries.PLACED_FEATURE, ResourceLocation.parse(str2))
        ));
        return map;
    }

    @ApiStatus.Internal
    public static void doBiomeModifications(MinecraftServer server) {
        // TODO
        getAddedFeaturesMap().forEach((biomeTag, featureTagKey) -> {
            var placedFeatureRegistry = server.registryAccess().get(Registries.PLACED_FEATURE).orElseThrow().value();

            for (var featureHolder : placedFeatureRegistry.getTagOrEmpty(featureTagKey)) {
                BiomeModifications.addFeature(
                    biomeSelectionContext -> biomeSelectionContext.hasTag(biomeTag),
                    GenerationStep.Decoration.VEGETAL_DECORATION,
                    featureHolder.unwrapKey().orElseThrow()
                );
            }
        });
    }

    private static class GreeneriesFabricPlatform implements GreeneriesPlatform {

        @Override
        public void onServerStart(Consumer<MinecraftServer> consumer) {
            ServerLifecycleEvents.SERVER_STARTING.register(consumer::accept);
        }

        @Override
        public void onClientStart(Consumer<Minecraft> consumer) {
            ClientLifecycleEvents.CLIENT_STARTED.register(consumer::accept);
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public CreativeModeTab.Builder creativeTabBuilder() {
            return FabricItemGroup.builder();
        }

        @Override
        public void setRenderLayer(Supplier<Block> block, ChunkSectionLayer renderType) {
            onClientStart(mc -> BlockRenderLayerMap.putBlock(block.get(), renderType));
        }

        @Override
        public void setBlockColor(Supplier<Block> block, GBlockColor color) {
            onClientStart(mc -> ColorProviderRegistry.BLOCK.register(color::getColor, block.get()));
        }

    }
}
