package net.sterbendes.greeneries.neoforge.data;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sterbendes.greeneries.BiomeModifierFeatureEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class DataGenerator {

    static List<BiomeModifierFeatureEntry> featureEntryList;

    public static void onGatherData(@NotNull GatherDataEvent event) {
        var registries = event.getLookupProvider();
        event.getGenerator().addProvider(
            true,
            (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableSubProv::new, LootContextParamSets.BLOCK)
            ), registries)
        );
        event.createDatapackRegistryObjects(generateBiomeModifiers());
    }

    private static RegistrySetBuilder generateBiomeModifiers() {
        var builder = new RegistrySetBuilder();
        for (var entry : featureEntryList) {
            builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                var biomeRegistry = bootstrap.lookup(Registries.BIOME);
                var featureRegistry = bootstrap.lookup(Registries.PLACED_FEATURE);
                var key = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, entry.feature().location());

                bootstrap.register(key,
                    new BiomeModifiers.AddFeaturesBiomeModifier(biomeRegistry.getOrThrow(entry.biomes()),
                        HolderSet.direct(featureRegistry.getOrThrow(entry.feature())),
                        GenerationStep.Decoration.VEGETAL_DECORATION));
            });
        }
        return builder;
    }
}
