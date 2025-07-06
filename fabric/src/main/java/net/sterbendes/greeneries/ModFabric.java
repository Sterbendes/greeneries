package net.sterbendes.greeneries;

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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModFabric implements ModInitializer {

    public static final ResourceKey<PlacedFeature> GRASS_FEATURE_REGISTRY_KEY = ResourceKey.create(
        Registries.PLACED_FEATURE, ResourceLocation.parse("greeneries:patch_red_fescue"));

    @Override
    public void onInitialize() {
        GreeneriesMod.init(new GreeneriesFabricPlatform());

        BiomeModifications.addFeature(biomeSelectionContext ->
                biomeSelectionContext.getBiomeKey().location().toString().equals("minecraft:plains"),
            GenerationStep.Decoration.VEGETAL_DECORATION, GRASS_FEATURE_REGISTRY_KEY);
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
