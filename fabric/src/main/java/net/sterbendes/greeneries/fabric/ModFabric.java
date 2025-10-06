package net.sterbendes.greeneries.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.sterbendes.greeneries.GreeneriesMod;
import net.sterbendes.greeneries.GreeneriesPlatform;
import net.sterbendes.greeneries.blocks.ModBlockColors.GBlockColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GreeneriesMod.init(new GreeneriesFabricPlatform());
    }

    private static class GreeneriesFabricPlatform implements GreeneriesPlatform {

        public void addFeature(TagKey<Biome> tag, ResourceKey<PlacedFeature> feature,
                                       @Nullable TagKey<Biome> denied) {
            Predicate<BiomeSelectionContext> predicate =
                denied == null ? context -> context.hasTag(tag)
                    : context -> context.hasTag(tag) && !context.hasTag(denied);
            BiomeModifications.addFeature(predicate, GenerationStep.Decoration.VEGETAL_DECORATION, feature);
        }

        @Override
        public void onServerStart(Consumer<MinecraftServer> consumer) {
            ServerLifecycleEvents.SERVER_STARTING.register(consumer::accept);
        }

        @Override
        public void onClientStart(Consumer<Minecraft> consumer) {
            if (isClient()) ClientLifecycleEvents.CLIENT_STARTED.register(consumer::accept);
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
            if (isClient()) onClientStart(mc -> ColorProviderRegistry.BLOCK.register(color::getColor, block.get()));
        }
    }
}
