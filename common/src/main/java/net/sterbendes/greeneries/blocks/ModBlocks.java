package net.sterbendes.greeneries.blocks;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import java.util.function.Function;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static net.sterbendes.greeneries.GreeneriesMod.modID;
import static net.sterbendes.greeneries.GreeneriesMod.platform;
import static net.sterbendes.greeneries.blocks.ModBlockColors.*;

public abstract class ModBlocks {

    private static final Map<String, Holder<Block>> allGreeneriesBlocks = new LinkedHashMap<>();

    static {
        registerGrass("grass", "very_short", "bushy", "medium");
        registerGrass("red_fescue", "very_short", "short", "bushy", "medium");
        registerGrass("common_bent", VARYING_GRASS_BLOCK_COLOR, "very_short", "short", "bushy");
        registerGrass("blue_grass", "very_short", "short", "bushy");

        register("medium_eagle_fern", VARYING_FERN_BLOCK_COLOR,
            ModBlocks::createTallGrassBlock, ofFullCopy(Blocks.FERN));
        register("tall_eagle_fern", VARYING_FERN_BLOCK_COLOR,
            DoublePlantBlock::new, ofFullCopy(Blocks.LARGE_FERN));

        register("short_royal_fern", VARYING_FERN_BLOCK_COLOR,
            ModBlocks::createTallGrassBlock, ofFullCopy(Blocks.FERN));
        register("medium_royal_fern", VARYING_FERN_BLOCK_COLOR,
            ModBlocks::createTallGrassBlock, ofFullCopy(Blocks.FERN));
        register("tall_royal_fern", VARYING_FERN_BLOCK_COLOR,
            DoublePlantBlock::new, ofFullCopy(Blocks.LARGE_FERN));

        register("cattail", FOLIAGE_COLOR,
            ReedBlock::new, ofFullCopy(Blocks.TALL_SEAGRASS));
        register("reed", FOLIAGE_COLOR,
            ReedBlock::new, ofFullCopy(Blocks.TALL_SEAGRASS));
    }

    private static TallGrassBlock createTallGrassBlock(BlockBehaviour.Properties properties) {
        return new TallGrassBlock(properties) { };
    }

    public static void registerGrass(String name, String... variants) {
        registerGrass(name, VARYING_GRASS_BLOCK_COLOR, variants);
    }

    public static void registerGrass(String name, @Nullable BlockColor blockTint,
                                     String... variants) {
        for (var variant : variants) {
            register(
                variant + "_" + name,
                blockTint,
                ModBlocks::createTallGrassBlock, ofFullCopy(Blocks.SHORT_GRASS)
            );
        }
    }

    private static void register(String name, @Nullable BlockColor blockTint,
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
