package net.sterbendes.greeneries.blocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.sterbendes.greeneries.GreeneriesMod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.sterbendes.greeneries.GreeneriesMod.platform;
import static net.sterbendes.greeneries.blocks.ModBlockColors.*;

public abstract class ModBlocks {

    private static final Map<String, Holder<Block>> allGreeneriesBlocks = new LinkedHashMap<>();

    static {
        registerVariants("grass", "very_short", "bushy", "medium");
        registerVariants("red_fescue", "very_short", "short", "bushy", "medium");
        registerVariants("common_bent", VARYING_GRASS_BLOCK_COLOR, null, "very_short", "short", "bushy");
        registerVariants("blue_grass", "very_short", "short", "bushy");

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

        register("cattail", FOLIAGE_COLOR, null,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));
        register("reed", FOLIAGE_COLOR, null,
            () -> new ReedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_SEAGRASS)));
    }


    public static void registerVariants(String name, String... variants) {
        registerVariants(name, VARYING_GRASS_BLOCK_COLOR, GRASS_ITEM_COLOR, variants);
    }

    public static void registerVariants(String name, @Nullable GBlockColor blockTint, @Nullable GItemColor itemTint,
                                        String... variants) {
        for (var variant : variants) {
            register(
                variant + "_" + name,
                blockTint, itemTint,
                () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)) { }
            );
        }
    }

    private static void register(String name, @Nullable GBlockColor blockTint, @Nullable GItemColor itemTint,
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
