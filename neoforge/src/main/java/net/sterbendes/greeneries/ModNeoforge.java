package net.sterbendes.greeneries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(GreeneriesMod.modID)
public class ModNeoforge {

    public static IEventBus modEventBus;

    public ModNeoforge(IEventBus modEventBus) {
        ModNeoforge.modEventBus = modEventBus;

        GreeneriesMod.init(new GreeneriesNeoforgePlatform());
    }

    private static class GreeneriesNeoforgePlatform implements GreeneriesPlatform {

        @Override
        public <T> Holder<T> register(@NotNull Registry<T> registry, ResourceLocation rl, @NotNull Supplier<T> value) {
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
        public void setRenderLayer(Supplier<Block> block, RenderType renderType) {
            onClientStart(mc -> ItemBlockRenderTypes.setRenderLayer(block.get(), renderType));
        }

        @Override
        public void setBlockColor(Supplier<Block> block, BlockColor color) {
            modEventBus.addListener(RegisterColorHandlersEvent.Block.class, event -> event.register(color,
                block.get()));
        }

        @Override
        public void setItemColor(Supplier<ItemLike> item, ItemColor itemColor) {
            modEventBus.addListener(RegisterColorHandlersEvent.Item.class, event -> event.register(itemColor,
                item.get()));
        }
    }
}
