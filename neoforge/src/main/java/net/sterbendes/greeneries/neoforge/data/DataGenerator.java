package net.sterbendes.greeneries.neoforge.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.sterbendes.greeneries.BiomeModifierFeatureEntry;
import net.sterbendes.greeneries.GreeneriesMod;
import net.sterbendes.greeneries.blocks.ModBlocks;
import net.sterbendes.greeneries.neoforge.ModNeoforge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DataGenerator {

    static List<BiomeModifierFeatureEntry> featureEntryList = new ArrayList<>();

    public static void onGatherData(@NotNull GatherDataEvent event) {
        var registries = event.getLookupProvider();
        var generator = event.getGenerator();

        generator.addProvider(
            true,
            (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableSubProv::new, LootContextParamSets.BLOCK)
            ), registries)
        );
        generator.addProvider(true, provideFlowerBlockStates(event.getExistingFileHelper()));
        generator.addProvider(true, provideDataMaps(generator.getPackOutput(), event.getLookupProvider()));
        generator.addProvider(true, provideTags(generator.getPackOutput(), event.getLookupProvider(),
            event.getExistingFileHelper()));
        event.createDatapackRegistryObjects(generateBiomeModifiers());
    }

    private static DataMapProvider provideDataMaps(PackOutput output,
                                                   CompletableFuture<HolderLookup.Provider> provider) {
        return new DataMapProvider(output, provider) {
            @Override
            protected void gather(HolderLookup.@NotNull Provider provider) {
                for (var entry : ModNeoforge.compostables.object2FloatEntrySet()) {
                    this.builder(NeoForgeDataMaps.COMPOSTABLES)
                        .add(entry.getKey().value().asItem().builtInRegistryHolder(),
                            new Compostable(entry.getFloatValue()), false);
                }
            }
        };
    }

    private static TagsProvider<Block> provideTags(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> provider,
        ExistingFileHelper existingFileHelper
    ) {
        return new TagsProvider<>(output, Registries.BLOCK, provider, GreeneriesMod.modID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.@NotNull Provider provider) {
                var smallFlowerBuilder = TagBuilder.create();
                for (Holder<Block> smallFlower : ModBlocks.small_flowers) {
                    smallFlowerBuilder.addElement(Objects.requireNonNull(smallFlower.getKey()).location());
                }
                var verySmallFlowerBuilder = TagBuilder.create();
                for (Holder<Block> verySmallFlower : ModBlocks.small_flowers) {
                    verySmallFlowerBuilder.addElement(Objects.requireNonNull(verySmallFlower.getKey()).location());
                }
                this.builders.put(
                    ResourceLocation.fromNamespaceAndPath(GreeneriesMod.modID, "small_flowers"),
                    smallFlowerBuilder);
                this.builders.put(
                    ResourceLocation.fromNamespaceAndPath(GreeneriesMod.modID, "very_small_flowers"),
                    verySmallFlowerBuilder
                );
            }
        };
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
                for (var blockHolder : ModBlocks.grass_variants) {
                    var builder = this.getVariantBuilder(blockHolder.value());
                    var modelLocation = Objects.requireNonNull(blockHolder.getKey()).location().withPrefix("block/");

                    builder.forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(new ModelFile.ExistingModelFile(modelLocation, helper)).build());
                    this.registeredBlocks.put(blockHolder.value(), builder);
                }

                for (var blockHolder : ModBlocks.small_flowers) {
                    var location = Objects.requireNonNull(blockHolder.getKey()).location();
                    this.itemModels().generatedModels.put(
                        location,
                        new ItemModelBuilder(location.withPrefix("item/"), helper)
                            .texture("layer0", location.withPrefix("block/small_flowers/").withSuffix("1"))
                            .parent(new ModelFile.ExistingModelFile(
                                ResourceLocation.withDefaultNamespace("item/generated"),
                                helper
                            ))
                    );
                    register(blockHolder.value(), location, "block/small_flowers/", "1");
                    register(blockHolder.value(), location, "block/small_flowers/", "2");
                }
                for (var blockHolder : ModBlocks.very_small_flowers) {
                    var location = Objects.requireNonNull(blockHolder.getKey()).location();
                    this.itemModels().generatedModels.put(
                        location,
                        new ItemModelBuilder(location.withPrefix("item/"), helper)
                            .texture("layer0", location.withPrefix("block/very_small_flowers/").withSuffix("2"))
                            .parent(new ModelFile.ExistingModelFile(
                                ResourceLocation.withDefaultNamespace("item/generated"),
                                helper
                            ))
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
