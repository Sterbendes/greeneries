package net.sterbendes.greeneries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.sterbendes.greeneries.blocks.ModBlockColors.GBlockColor;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface GreeneriesPlatform {

    default <T> Holder<T> register(Registry<T> registry, ResourceLocation rl, Supplier<T> value) {
        return Registry.registerForHolder(registry, rl, value.get());
    }

    @Contract(value = " -> new", pure = true)
    CreativeModeTab.Builder creativeTabBuilder();

    void setRenderLayer(Supplier<Block> block, RenderType renderType);

    void onServerStart(Consumer<MinecraftServer> consumer);

    void onClientStart(Consumer<Minecraft> consumer);

    void setBlockColor(Supplier<Block> block, GBlockColor color);

    void setItemColor(Supplier<ItemLike> item, ItemColor itemColor);

    default boolean isClient() {
        try {
            Class.forName("net.minecraft.client.Minecraft");
            return true;
        } catch (ClassNotFoundException | RuntimeException e) {
            return false;
        }
    }
}
