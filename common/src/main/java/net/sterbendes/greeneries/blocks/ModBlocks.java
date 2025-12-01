package net.sterbendes.greeneries.blocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.sterbendes.greeneries.GreeneriesMod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

import static net.sterbendes.greeneries.GreeneriesMod.platform;
import static net.sterbendes.greeneries.blocks.ModBlockColors.*;

public abstract class ModBlocks {

    private static final Map<String, Holder<Block>> allGreeneriesBlocks = new LinkedHashMap<>();

    public static final List<Holder<Block>> generateSimpleBlockstates = new ArrayList<>();

    public static final List<Holder<Block>> small_flowers = new ArrayList<>();

    public static final List<Holder<Block>> very_small_flowers = new ArrayList<>();


    static {
        // GRASS VARIANTS
        registerSimpleVariants("grass",
            "very_short", "bushy", "medium");

        registerSimpleVariants("red_fescue",
            "very_short", "short", "bushy", "medium");

        registerVariants("common_bent",
            VARYING_GRASS_BLOCK_COLOR, null, true,
            "very_short", "short", "bushy");

        registerSimpleVariants("blue_grass",
            "very_short", "short", "bushy");

        // FERN VARIANTS
        register("medium_eagle_fern", VARYING_FERN_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FERN)) { });
        register("tall_eagle_fern", VARYING_FERN_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new DoublePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_FERN)));

        register("short_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FERN)) { });
        register("medium_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FERN)) { });
        register("tall_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            () -> new DoublePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_FERN)) { });

        // REEDS
        register("cattail", FOLIAGE_COLOR, null,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));
        register("reed", FOLIAGE_COLOR, null,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));

        register("small_allium", null, null,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY)) { });

        registerFlowers();
    }

    private static void registerFlowers() {
        var flowerNames = List.of("allium", "azure_bluet", "blue_orchid", "cornflower", "dandelion", "lily_of_the_valley",
            "orange_tulip", "oxeye_daisy", "pink_tulip", "poppy", "red_tulip", "white_tulip");

        for (Block block : BuiltInRegistries.BLOCK) {
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            if (!flowerNames.contains(path)) continue;

            var flowerBlock = (FlowerBlock) block;
            small_flowers.add(register("small_" + path, null, null,
                () -> new FlowerBlock(flowerBlock.getSuspiciousEffects(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))));
            very_small_flowers.add(register("very_small_" + path, null, null,
                () -> new FlowerBlock(flowerBlock.getSuspiciousEffects(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY))));
        }
//
//        for (var blockHolder : small_flowers) {
//            generateSimpleBlockstates.put(blockHolder, new ResourceLocation[]{
//                blockHolder.unwrapKey().orElseThrow().location().withSuffix("1"),
//                blockHolder.unwrapKey().orElseThrow().location().withSuffix("2")
//            });
//        }
//        for (var blockHolder : very_small_flowers) {
//            generateSimpleBlockstates.put(blockHolder, new ResourceLocation[]{
//                blockHolder.unwrapKey().orElseThrow().location().withSuffix("1"),
//                blockHolder.unwrapKey().orElseThrow().location().withSuffix("2"),
//                blockHolder.unwrapKey().orElseThrow().location().withSuffix("3")
//            });
//        }
    }


    public static void registerSimpleVariants(String name, String... variants) {
        registerVariants(name, VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR, true, variants);
    }

    public static void registerVariants(String name, @Nullable GBlockColor blockTint, @Nullable GItemColor itemTint,
                                        boolean generateBlockState, String... variants) {
        for (var variant : variants) {
            var holder = register(
                variant + "_" + name,
                blockTint, itemTint,
                () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { }
            );
            if (generateBlockState)
                generateSimpleBlockstates.add(holder);
        }
    }

    private static Holder<Block> register(String name, @Nullable GBlockColor blockTint, @Nullable GItemColor itemTint,
                                          Supplier<Block> blockSupplier) {
        var holder = GreeneriesMod.register(name, BuiltInRegistries.BLOCK, blockSupplier);
        GreeneriesMod.register(
            name, BuiltInRegistries.ITEM,
            () -> new BlockItem(holder.value(), new Item.Properties())
        );

        if (platform.isClient()) platform.setRenderLayer(holder::value, RenderType.cutout());
        if (blockTint != null && platform.isClient()) platform.setBlockColor(holder::value, blockTint);
        if (itemTint != null && platform.isClient()) platform.setItemColor(holder::value, itemTint::getColor);

        allGreeneriesBlocks.put(name, holder);
        return holder;
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
