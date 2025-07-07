package net.sterbendes.greeneries;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.sterbendes.greeneries.GreeneriesMod.platform;

public abstract class ModBlocks {

    private static final Map<String, Holder<Block>> allGreeneriesBlocks = new LinkedHashMap<>();

    public static final BlockColor VARYING_GRASS_BLOCK_COLOR = (blockState, blockAndTintGetter, blockPos, i) -> {
        var color = blockAndTintGetter != null && blockPos != null
            ? BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos)
            : GrassColor.getDefaultColor();

        var randomSource = RandomSource.create(blockPos != null ? blockPos.asLong() : i);

        var rand1 = (randomSource.nextInt() / 20) >> 8 & 0b11111111_00000000_00000000;
        var rand2 = (randomSource.nextInt() / 16) >> 16 & 0b00000000_11111111_00000000;
        var rand3 = (randomSource.nextInt() / 24) >> 24 & 0b00000000_00000000_11111111;

        return color + rand1 + rand2 + rand3;
    };

    public static final ItemColor PLAINS_FOLIAGE_COLOR = (stack, i) -> FoliageColor.get(0.8, 0.4);


    static {
        registerGrass("red_fescue", "very_short", "short", "bushy", "medium");
        registerGrass("common_bent", "very_short", "short", "bushy");
    }


    public static void registerGrass(String name, String... variants) {
        for (var variant : variants) {
            register(variant + "_" + name, true, RenderType.cutout(),
                () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { },
                VARYING_GRASS_BLOCK_COLOR, PLAINS_FOLIAGE_COLOR);
        }
    }

    private static @NotNull Holder<Block> register(String name, boolean registerItem, RenderType renderType,
                                                   Supplier<Block> block, BlockColor color, ItemColor itemColor) {
        var holder = GreeneriesMod.register(name, BuiltInRegistries.BLOCK, block);
        if (registerItem)
            GreeneriesMod.register(
                name, BuiltInRegistries.ITEM,
                () -> new BlockItem(holder.value(), new Item.Properties())
            );

        platform.setRenderLayer(holder::value, renderType);
        platform.setBlockColor(holder::value, color);
        if (registerItem) platform.setItemColor(holder::value, itemColor);

        allGreeneriesBlocks.put(name, holder);
        return holder;
    }

    public static Collection<Holder<Block>> getAllGreeneriesBlocks() {
        return allGreeneriesBlocks.values();
    }

    public static Holder<Block> get(String name) {
        return allGreeneriesBlocks.get(name);
    }

    static void init() { }
}
