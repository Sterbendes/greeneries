package net.sterbendes.greeneries.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.sterbendes.greeneries.GreeneriesMod;
import net.sterbendes.greeneries.GreeneriesPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GreeneriesMod.init(new GreeneriesFabricPlatform());
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
        getAddedFeaturesMap().forEach((biomeTag, featureTagKey) -> {
            var placedFeatureRegistry = server.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);

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
        public void onServerStart(@NotNull Consumer<MinecraftServer> consumer) {
            ServerLifecycleEvents.SERVER_STARTING.register(consumer::accept);
        }

        @Override
        public void onClientStart(@NotNull Consumer<Minecraft> consumer) {
            ClientLifecycleEvents.CLIENT_STARTED.register(consumer::accept);
        }

        @Contract(value = " -> new", pure = true)
        @Override
        public CreativeModeTab.@NotNull Builder creativeTabBuilder() {
            return FabricItemGroup.builder();
        }

        @Override
        public void setRenderLayer(Supplier<Block> block, RenderType renderType) {
            onClientStart(mc -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), renderType));
        }

        @Override
        public void setBlockColor(Supplier<Block> block, BlockColor color) {
            onClientStart(mc -> ColorProviderRegistry.BLOCK.register(color, block.get()));
        }

        @Override
        public void setItemColor(Supplier<ItemLike> item, ItemColor itemColor) {
            onClientStart(mc -> ColorProviderRegistry.ITEM.register(itemColor, item.get()));
        }
    }
}
