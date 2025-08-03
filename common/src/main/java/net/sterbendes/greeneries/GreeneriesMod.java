package net.sterbendes.greeneries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.sterbendes.greeneries.blocks.ModBlocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Supplier;

public class GreeneriesMod {

    public static final String modID = "greeneries";
    @UnknownNullability
    @ApiStatus.Internal
    public static GreeneriesPlatform platform;

    @ApiStatus.Internal
    public static void init(GreeneriesPlatform platform) {
        GreeneriesMod.platform = platform;
        ModBlocks.init();
        ModCreativeTabs.init();

        platform.setBlockColor(() -> Blocks.SHORT_GRASS, ModBlocks.VARYING_GRASS_BLOCK_COLOR);
        platform.setBlockColor(() -> Blocks.TALL_GRASS, ModBlocks.VARYING_GRASS_BLOCK_COLOR);
        platform.setBlockColor(() -> Blocks.FERN, ModBlocks.VARYING_FERN_BLOCK_COLOR);
        platform.setBlockColor(() -> Blocks.LARGE_FERN, ModBlocks.VARYING_FERN_BLOCK_COLOR);
    }

    @ApiStatus.Internal
    public static <T> Holder<T> register(String name, Registry<T> registry, Supplier<T> obj) {
        return platform.register(registry, ResourceLocation.fromNamespaceAndPath(modID, name), obj);
    }
}
