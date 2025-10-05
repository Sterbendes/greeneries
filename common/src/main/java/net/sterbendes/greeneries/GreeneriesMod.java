package net.sterbendes.greeneries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.sterbendes.greeneries.blocks.ModBlockColors;
import net.sterbendes.greeneries.blocks.ModBlocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
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

        registerBiomeModifiers(platform);
        setVanillaBlockColors(platform);
    }

    private static void setVanillaBlockColors(GreeneriesPlatform platform) {
        platform.setBlockColor(() -> Blocks.SHORT_GRASS, ModBlockColors.VARYING_GRASS_BLOCK_COLOR);
        platform.setBlockColor(() -> Blocks.TALL_GRASS, ModBlockColors.VARYING_GRASS_BLOCK_COLOR);
        platform.setBlockColor(() -> Blocks.FERN, ModBlockColors.VARYING_FERN_BLOCK_COLOR);
        platform.setBlockColor(() -> Blocks.LARGE_FERN, ModBlockColors.VARYING_FERN_BLOCK_COLOR);
    }

    @ApiStatus.Internal
    public static <T> Holder<T> register(String name, Registry<T> registry, Supplier<T> obj) {
        return platform.register(registry, ResourceLocation.fromNamespaceAndPath(modID, name), obj);
    }

    private static void registerBiomeModifiers(GreeneriesPlatform platform) {
        var variants = List.of("red_fescue", "common_bent_grass", "blue_grass");

        var noVariants = List.of("reed", "royal_fern");

        for (String str : variants) {
            var commonTag = TagKey.create(Registries.BIOME, ResourceLocation.parse("greeneries:has_common_" + str));
            var rareTag = TagKey.create(Registries.BIOME, ResourceLocation.parse("greeneries:has_rare_" + str));
            var commonFeature = ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.parse("greeneries:patch_" + str + "_common"));
            var rareFeature = ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.parse("greeneries:patch_" + str + "_rare"));

            platform.addFeature(commonTag, commonFeature, null);
            platform.addFeature(rareTag, rareFeature, commonTag);
        }
        for (String str : noVariants) {
            var tag = TagKey.create(Registries.BIOME, ResourceLocation.parse("greeneries:has_" + str));
            var feature = ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.parse("greeneries:patch_" + str));
            platform.addFeature(tag, feature, null);
        }
    }
}
