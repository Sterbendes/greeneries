package net.sterbendes.greeneries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record BiomeModifierFeatureEntry(
    TagKey<Biome> biomes,
    TagKey<Biome> deniedBiomes,
    ResourceKey<PlacedFeature> feature
) { }
