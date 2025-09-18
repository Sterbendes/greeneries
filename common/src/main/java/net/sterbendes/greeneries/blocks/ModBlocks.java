package net.sterbendes.greeneries.blocks;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.sterbendes.greeneries.GreeneriesMod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static net.sterbendes.greeneries.GreeneriesMod.modID;
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

    public static final BlockColor VARYING_FERN_BLOCK_COLOR = (blockState, blockAndTintGetter, blockPos, i) -> {
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

    public static final BlockColor FOLIAGE_COLOR = (blockState, blockAndTintGetter, blockPos, i) ->
        blockAndTintGetter != null && blockPos != null
            ? BiomeColors.getAverageFoliageColor(blockAndTintGetter, blockPos)
            : FoliageColor.FOLIAGE_DEFAULT;

    public static final ItemTintSource GRASS_ITEM_COLOR = new GrassColorSource();


    static {
        Function<BlockBehaviour.Properties, Block> tallGrassBlock = properties -> new TallGrassBlock(properties) {};

        registerVariants("grass", "very_short", "bushy", "medium");
        registerVariants("red_fescue", "very_short", "short", "bushy", "medium");
        registerVariants("common_bent", VARYING_GRASS_BLOCK_COLOR, null, "very_short", "short", "bushy");
        registerVariants("blue_grass", "very_short", "short", "bushy");

        register("medium_eagle_fern", VARYING_FERN_BLOCK_COLOR, GRASS_ITEM_COLOR,
            tallGrassBlock, ofFullCopy(Blocks.FERN));
        register("tall_eagle_fern", VARYING_FERN_BLOCK_COLOR, GRASS_ITEM_COLOR,
            DoublePlantBlock::new, ofFullCopy(Blocks.LARGE_FERN));

        register("short_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            tallGrassBlock, ofFullCopy(Blocks.FERN));
        register("medium_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            tallGrassBlock, ofFullCopy(Blocks.FERN));
        register("tall_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            DoublePlantBlock::new, ofFullCopy(Blocks.LARGE_FERN));

        register("cattail", FOLIAGE_COLOR, null,
            ReedBlock::new, ofFullCopy(Blocks.TALL_SEAGRASS));
        register("reed", FOLIAGE_COLOR, null,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS).setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(modID, "")))));
    }


    public static void registerVariants(String name, String... variants) {
        registerVariants(name, VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR, variants);
    }

    public static void registerVariants(String name, @Nullable BlockColor blockTint, @Nullable ItemTintSource itemTint,
                                        String... variants) {
        for (var variant : variants) {
            register(
                variant + "_" + name,
                blockTint, itemTint,
                GrassBlock::new, ofFullCopy(Blocks.SHORT_GRASS)
            );
        }
    }

    private static void register(String name, @Nullable BlockColor blockTint, @Nullable ItemTintSource itemTint,
                                 Function<BlockBehaviour.Properties, Block> blockSupplier,
                                 BlockBehaviour.Properties properties) {
        var id = ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(modID, name));
        properties.setId(id);
        var holder = GreeneriesMod.register(name, BuiltInRegistries.BLOCK, () -> blockSupplier.apply(properties));
        GreeneriesMod.register(
            name, BuiltInRegistries.ITEM,
            () -> new BlockItem(holder.value(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(modID, name))))
        );

        platform.setRenderLayer(holder::value, ChunkSectionLayer.CUTOUT);
        if (blockTint != null) platform.setBlockColor(holder::value, blockTint);
        if (itemTint != null) platform.setItemColor(ResourceLocation.fromNamespaceAndPath(modID, name), itemTint);

        allGreeneriesBlocks.put(name, holder);
    }

    public static Collection<Holder<Block>> getAllGreeneriesBlocks() {
        return allGreeneriesBlocks.values();
    }

    public static Holder<Block> get(String name) {
        return allGreeneriesBlocks.get(name);
    }

    @SuppressWarnings("EmptyMethod")
    @ApiStatus.Internal
    public static void init() { }
}
