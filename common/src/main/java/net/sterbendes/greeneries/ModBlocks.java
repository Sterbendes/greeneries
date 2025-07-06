package net.sterbendes.greeneries;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static net.sterbendes.greeneries.GreeneriesMod.platform;

public abstract class ModBlocks {

    public static final BlockColor GRASS_BLOCK_COLOR =
        (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter != null && blockPos != null
            ? BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos)
            : FoliageColor.getDefaultColor();

    public static final ItemColor PLAINS_FOLIAGE_COLOR = (stack, i) -> FoliageColor.get(0.8, 0.4);


    public static Holder<Block> red_fescue = register("red_fescue", true, RenderType.cutout(),
        () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { },
        GRASS_BLOCK_COLOR, PLAINS_FOLIAGE_COLOR);


    private static @NotNull Holder<Block> register(String name, boolean registerItem, RenderType renderType,
                                                   Supplier<Block> block, BlockColor color, ItemColor itemColor) {
        var holder = register(name, registerItem, block);

        platform.setRenderLayer(holder::value, renderType);
        platform.setBlockColor(holder::value, color);
        if (registerItem) platform.setItemColor(holder::value, itemColor);

        return holder;
    }

    private static @NotNull Holder<Block> register(String name, boolean registerItem, Supplier<Block> block) {
        var blockHolder = GreeneriesMod.register(name, BuiltInRegistries.BLOCK, block);
        if (registerItem)
            GreeneriesMod.register(
                name, BuiltInRegistries.ITEM,
                () -> new BlockItem(blockHolder.value(), new Item.Properties())
            );
        return blockHolder;
    }

    static void init() { }
}
