package net.sterbendes.greeneries.neoforge.data;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sterbendes.greeneries.BiomeModifierFeatureEntry;
import net.sterbendes.greeneries.GreeneriesMod;
import net.sterbendes.greeneries.blocks.ModBlocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        event.getGenerator().addProvider(true, provideFlowerBlockStates(event.getExistingFileHelper()));
        event.createDatapackRegistryObjects(generateBiomeModifiers());
    }

    private static DataProvider.@NotNull Factory<BlockStateProvider> provideFlowerBlockStates(
        @NotNull ExistingFileHelper helper
    ) {
        return output -> new BlockStateProvider(output, GreeneriesMod.modID, helper) {

            private void register(Block block, ResourceLocation location, String prefix, String suffix) {
                location = location.withPrefix(prefix).withSuffix(suffix);
                this.models().generatedModels.put(
                    location,
                    new BlockModelBuilder(location, helper)
                        .texture("cross", location)
                        .parent(new ModelFile.ExistingModelFile(
                            ResourceLocation.withDefaultNamespace("block/cross"),
                            helper
                        ))
                );
                this.getVariantBuilder(block).addModels(this.getVariantBuilder(block).partialState(),
                    ConfiguredModel.builder().modelFile(new ModelFile.ExistingModelFile(location, helper)).build());
            }

            @Override
            protected void registerStatesAndModels() {
                for (var blockHolder : ModBlocks.generateSimpleBlockstates) {
                    var builder = this.getVariantBuilder(blockHolder.value());
                    var modelLocation = Objects.requireNonNull(blockHolder.getKey()).location().withPrefix("block/");

                    builder.forAllStates(state -> ConfiguredModel.builder()
                            .modelFile(new ModelFile.ExistingModelFile(modelLocation, helper)).build());
                    this.registeredBlocks.put(blockHolder.value(), builder);
                }

                for (var blockHolder : ModBlocks.small_flowers) {
                    var location = Objects.requireNonNull(blockHolder.getKey()).location();
                    this.simpleBlockItem(
                        blockHolder.value(),
                        new ModelFile.ExistingModelFile(blockHolder.getKey().location().withPrefix("block" +
                            "/small_flowers/").withSuffix("1"), helper)
                    );
                    register(blockHolder.value(), location, "block/small_flowers/", "1");
                    register(blockHolder.value(), location, "block/small_flowers/", "2");
                }
                for (var blockHolder : ModBlocks.very_small_flowers) {
                    var location = Objects.requireNonNull(blockHolder.getKey()).location();
                    this.simpleBlockItem(
                        blockHolder.value(),
                        new ModelFile.ExistingModelFile(blockHolder.getKey().location().withPrefix("block" +
                            "/very_small_flowers/").withSuffix("1"), helper)
                    );
                    register(blockHolder.value(), location, "block/very_small_flowers/", "1");
                    register(blockHolder.value(), location, "block/very_small_flowers/", "2");
                    register(blockHolder.value(), location, "block/very_small_flowers/", "3");
                }
            }
        };
    }

    @ApiStatus.Internal
    public static void registerBiomeModifierEntry(BiomeModifierFeatureEntry entry) {
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
