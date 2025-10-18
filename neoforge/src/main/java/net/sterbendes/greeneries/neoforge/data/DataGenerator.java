package net.sterbendes.greeneries.neoforge.data;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sterbendes.greeneries.BiomeModifierFeatureEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataGenerator {

    static List<BiomeModifierFeatureEntry> featureEntryList = new ArrayList<>();

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

    @ApiStatus.Internal
    public static void registerEntry(BiomeModifierFeatureEntry entry) {
        featureEntryList.add(entry);
    }

    private static RegistrySetBuilder generateBiomeModifiers() {
        var builder = new RegistrySetBuilder();
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
            for (var entry : featureEntryList) {
                var key = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, entry.feature().location());

                bootstrap.register(key, createBiomeModifier(entry, bootstrap));
            }
        });
        return builder;
    }

    private static BiomeModifier createBiomeModifier(BiomeModifierFeatureEntry entry,
                                                     BootstrapContext<BiomeModifier> bootstrap) {
        var biomeRegistry = bootstrap.lookup(Registries.BIOME);
        var featureRegistry = bootstrap.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> biomeHolderSet = biomeRegistry.get(entry.biomes()).orElseThrow();
//        if (entry.deniedBiomes() != null)
//            //noinspection unchecked
//            biomeHolderSet = HolderSet.direct(biomeHolderSet.stream()
//                .filter(biomeHolder -> !biomeHolder.is(entry.deniedBiomes())).toArray(Holder[]::new));
        var featureHolder = HolderSet.direct(featureRegistry.get(entry.feature()).orElseThrow());

        return new BiomeModifiers.AddFeaturesBiomeModifier(biomeHolderSet, featureHolder,
            GenerationStep.Decoration.VEGETAL_DECORATION);
    }
}
