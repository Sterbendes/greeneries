package net.sterbendes.greeneries.blocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import java.util.stream.Stream;

import static net.sterbendes.greeneries.GreeneriesMod.platform;
import static net.sterbendes.greeneries.blocks.ModBlockColors.*;

public abstract class ModBlocks {

    private static final Map<String, Holder<Block>> ALL_GREENERIES_BLOCKS = new LinkedHashMap<>();

    public static final List<Holder<Block>> GRASS_VARIANTS = new ArrayList<>();

    public static final List<FlowerBlock> FLOWERS = Stream.of("allium", "azure_bluet", "blue_orchid", "cornflower",
            "dandelion", "lily_of_the_valley", "orange_tulip", "oxeye_daisy", "pink_tulip", "poppy", "red_tulip",
            "white_tulip").map(s -> BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace(s)))
        .map(block -> (FlowerBlock) block).toList();

    public static final List<Holder<Block>> SMALL_FLOWERS = new ArrayList<>();

    public static final List<Holder<Block>> VERY_SMALL_FLOWERS = new ArrayList<>();


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

        register("bushy_moss_carpet", null, null,
            () -> new CarpetBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_CARPET)));
    }

    private static void registerVanillaGrassVariants() {
        GRASS_VARIANTS.add(register(
            "very_short_grass",
            VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new GreeneriesGrassBlock(Blocks.SHORT_GRASS, Blocks.SHORT_GRASS)
        ));
        GRASS_VARIANTS.add(register(
            "bushy_grass",
            VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { }
        ));
        GRASS_VARIANTS.add(register(
            "medium_grass",
            VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR,
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { }
        ));
    }

    private static void registerFlowers() {
        for (var flowerBlock : FLOWERS) {
            String path = BuiltInRegistries.BLOCK.getKey(flowerBlock).getPath();

            SMALL_FLOWERS.add(register("small_" + path, null, null,
                () -> new FlowerBlock(flowerBlock.getSuspiciousEffects(),
                    BlockBehaviour.Properties.ofFullCopy(flowerBlock))));
            VERY_SMALL_FLOWERS.add(register("very_small_" + path, null, null,
                () -> new FlowerBlock(flowerBlock.getSuspiciousEffects(),
                    BlockBehaviour.Properties.ofFullCopy(flowerBlock))));
        }
    }


    public static void registerGrassVariants(String name, String... variants) {
        registerGrassVariants(name, VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR, true, variants);
    }

    public static void registerGrassVariants(String name, @Nullable GBlockColor blockTint,
                                             @Nullable GItemColor itemTint,
                                             boolean generateBlockState, String... variants) {
        for (int i = 0; i < variants.length; i++) {
            var variant = variants[i];
            var nextVariant = i + 1 < variants.length ? variants[i + 1] + "_" + name : null;

            var holder = register(
                variant + "_" + name,
                blockTint, itemTint,
                () -> new GreeneriesGrassBlock(Blocks.SHORT_GRASS, () -> get(nextVariant).value().defaultBlockState())
            );
            if (generateBlockState)
                GRASS_VARIANTS.add(holder);
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

        ALL_GREENERIES_BLOCKS.put(name, holder);
        return holder;
    }

    public static Collection<Holder<Block>> getAllGreeneriesBlocks() {
        return ALL_GREENERIES_BLOCKS.values();
    }

    public static Holder<Block> get(@Nullable String name) {
        return ALL_GREENERIES_BLOCKS.get(name);
    }

    public static Collection<Holder<Block>> getFiltered(String contains) {
        return ALL_GREENERIES_BLOCKS.entrySet().stream()
            .filter(it -> it.getKey().contains(contains))
            .map(Map.Entry::getValue).collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("EmptyMethod")
    @ApiStatus.Internal
    public static void init() { }
}
