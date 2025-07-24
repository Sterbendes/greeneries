package net.sterbendes.greeneries;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

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

        if (blockPos == null) return color;
        var randomSource = RandomSource.create(BlockPos.asLong(blockPos.getX(), 0, blockPos.getZ()));

        var rand1 = (randomSource.nextInt() / 22) >> 8 & 0b11111111_00000000_00000000; // red
        var rand2 = (randomSource.nextInt() / 18) >> 16 & 0b00000000_11111111_00000000; // green
        var rand3 = (randomSource.nextInt() / 24) >> 24 & 0b00000000_00000000_11111111; // blue

        return color + rand1 + rand2 + rand3;
    };

    public static final ItemColor GRASS_ITEM_COLOR = (stack, i) -> GrassColor.getDefaultColor();


    static {
        registerVariants("grass", "very_short", "bushy", "medium");
        registerVariants("red_fescue", "very_short", "short", "bushy", "medium");
        registerVariants("common_bent", VARYING_GRASS_BLOCK_COLOR, null, "very_short", "short", "bushy");

        register("cattail", VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));
        register("reed", VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));
    }


    public static void registerVariants(String name, String... variants) {
        registerVariants(name, VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR, variants);
    }

    public static void registerVariants(String name, @Nullable BlockColor blockTint, @Nullable ItemColor itemTint,
                                        String... variants) {
        for (var variant : variants) {
            register(
                variant + "_" + name,
                blockTint, itemTint,
                () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) {}
            );
        }
    }

    private static void register(String name, @Nullable BlockColor blockTint, @Nullable ItemColor itemTint,
                                 Supplier<Block> blockSupplier) {
        var holder = GreeneriesMod.register(name, BuiltInRegistries.BLOCK, blockSupplier);
        GreeneriesMod.register(
            name, BuiltInRegistries.ITEM,
            () -> new BlockItem(holder.value(), new Item.Properties())
        );

        platform.setRenderLayer(holder::value, RenderType.cutout());
        if (blockTint != null) platform.setBlockColor(holder::value, blockTint);
        if (itemTint != null) platform.setItemColor(holder::value, itemTint);

        allGreeneriesBlocks.put(name, holder);
    }

    public static Collection<Holder<Block>> getAllGreeneriesBlocks() {
        return allGreeneriesBlocks.values();
    }

    public static Holder<Block> get(String name) {
        return allGreeneriesBlocks.get(name);
    }

    static void init() { }
}
