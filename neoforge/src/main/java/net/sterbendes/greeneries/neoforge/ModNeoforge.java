package net.sterbendes.greeneries.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.sterbendes.greeneries.BiomeModifierFeatureEntry;
import net.sterbendes.greeneries.GreeneriesMod;
import net.sterbendes.greeneries.GreeneriesPlatform;
import net.sterbendes.greeneries.blocks.ModBlockColors.GBlockColor;
import net.sterbendes.greeneries.neoforge.data.DataGenerator;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(GreeneriesMod.modID)
public class ModNeoforge {

    public static @UnknownNullability IEventBus modEventBus;

    public ModNeoforge(IEventBus modEventBus) {
        ModNeoforge.modEventBus = modEventBus;

        modEventBus.addListener(GatherDataEvent.Server.class, DataGenerator::onGatherData);

        GreeneriesMod.init(new GreeneriesNeoforgePlatform());
    }

    private static class GreeneriesNeoforgePlatform implements GreeneriesPlatform {

        @Override
        public boolean isClient() {
            return FMLEnvironment.getDist().isClient();
        }

        @Override
        public void addFeature(TagKey<Biome> biomes, ResourceKey<PlacedFeature> feature,
                               @Nullable TagKey<Biome> deniedBiomes) {
            DataGenerator.registerEntry(new BiomeModifierFeatureEntry(biomes, deniedBiomes, feature));
        }

        public <T> Holder<T> register(Registry<T> registry, ResourceLocation rl, Supplier<T> value) {
            modEventBus.<RegisterEvent>addListener(event -> event.register(registry.key(), rl, value));
            return DeferredHolder.create(registry.key(), rl);
        }

        @Override
        public CreativeModeTab.Builder creativeTabBuilder() {
            return CreativeModeTab.builder();
        }

        @Override
        public void onServerStart(Consumer<MinecraftServer> consumer) {
            NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, event -> consumer.accept(event.getServer()));
        }

        @Override
        public void onClientStart(Consumer<Minecraft> consumer) {
            modEventBus.addListener(FMLClientSetupEvent.class, event -> consumer.accept(Minecraft.getInstance()));
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setRenderLayer(Supplier<Block> block, ChunkSectionLayer layer) {
            onClientStart(mc -> ItemBlockRenderTypes.setRenderLayer(block.get(), layer));
        }

        @Override
        public void setBlockColor(Supplier<Block> block, GBlockColor color) {
            modEventBus.addListener(RegisterColorHandlersEvent.Block.class, event -> event.register(color::getColor,
                block.get()));
        }

    }
}
