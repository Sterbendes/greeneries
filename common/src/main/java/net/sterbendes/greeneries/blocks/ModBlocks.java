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
import java.util.stream.Collectors;

import static net.sterbendes.greeneries.GreeneriesMod.platform;
import static net.sterbendes.greeneries.blocks.ModBlockColors.*;

public abstract class ModBlocks {

    private static final Map<String, Holder<Block>> allGreeneriesBlocks = new LinkedHashMap<>();

    public static final List<Holder<Block>> grass_variants = new ArrayList<>();

    public static final List<Holder<Block>> small_flowers = new ArrayList<>();

    public static final List<Holder<Block>> very_small_flowers = new ArrayList<>();


    // REEDS
    public static final Holder<Block> REED = register("reed", FOLIAGE_COLOR, null,
        () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));

    public static final Holder<Block> CATTAIL = register("cattail", FOLIAGE_COLOR, null,
        () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));

    static {
        // GRASS VARIANTS
        registerVanillaGrassVariants();

        registerGrassVariants("red_fescue",
            "very_short", "short", "bushy", "medium");

        registerGrassVariants("common_bent",
            VARYING_GRASS_BLOCK_COLOR, null, true,
            "very_short", "short", "bushy");

        registerGrassVariants("blue_grass",
            "very_short", "short", "bushy");

        // FERN VARIANTS
        var tallEagleFern = register("tall_eagle_fern", VARYING_FERN_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new DoublePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_FERN)));
        register("medium_eagle_fern", VARYING_FERN_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new GreeneriesGrassBlock(Blocks.FERN, tallEagleFern)
        );

        var tallRoyalFern = register("tall_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            () -> new DoublePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_FERN)) { });
        var mediumRoyalFern = register("medium_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            () -> new GreeneriesGrassBlock(Blocks.FERN, tallRoyalFern));
        register("short_royal_fern", VARYING_FERN_BLOCK_COLOR, null,
            () -> new GreeneriesGrassBlock(Blocks.FERN, mediumRoyalFern));


        registerFlowers();
    }

    private static void registerVanillaGrassVariants() {
        grass_variants.add(register(
            "very_short_grass",
            VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new GreeneriesGrassBlock(Blocks.SHORT_GRASS, Blocks.SHORT_GRASS)
        ));
        grass_variants.add(register(
            "bushy_grass",
            VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { }
        ));
        grass_variants.add(register(
            "medium_grass",
            VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { }
        ));
    }

    private static void registerFlowers() {
        var flowerNames = List.of("allium", "azure_bluet", "blue_orchid", "cornflower", "dandelion",
            "lily_of_the_valley", "orange_tulip", "oxeye_daisy", "pink_tulip", "poppy", "red_tulip", "white_tulip");

        for (Block block : BuiltInRegistries.BLOCK) {
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            if (!flowerNames.contains(path)) continue;

            var flowerBlock = (FlowerBlock) block;
            small_flowers.add(register("small_" + path, null, null,
                () -> new FlowerBlock(flowerBlock.getSuspiciousEffects(),
                    BlockBehaviour.Properties.ofFullCopy(flowerBlock))));
            very_small_flowers.add(register("very_small_" + path, null, null,
                () -> new FlowerBlock(flowerBlock.getSuspiciousEffects(),
                    BlockBehaviour.Properties.ofFullCopy(flowerBlock))));
        }
    }


    public static void registerGrassVariants(String name, String... variants) {
        registerGrassVariants(name, VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR, true, variants);
    }

    public static void registerGrassVariants(String name, @Nullable GBlockColor blockTint, @Nullable GItemColor itemTint,
                                             boolean generateBlockState, String... variants) {
        for (int i = 0; i < variants.length; i++) {
            var variant = variants[i];
            var next = i + 1 < variants.length ? variants[i + 1] + "_" + name : null;

            var holder = register(
                variant + "_" + name,
                blockTint, itemTint,
                () -> new GreeneriesGrassBlock(Blocks.SHORT_GRASS, get(next))
            );
            if (generateBlockState)
                grass_variants.add(holder);
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

    public static Holder<Block> get(@Nullable String name) {
        return allGreeneriesBlocks.get(name);
    }

    public static Collection<Holder<Block>> getFiltered(String contains) {
        return allGreeneriesBlocks.entrySet().stream()
            .filter(it -> it.getKey().contains(contains))
            .map(Map.Entry::getValue).collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("EmptyMethod")
    @ApiStatus.Internal
    public static void init() { }
}
