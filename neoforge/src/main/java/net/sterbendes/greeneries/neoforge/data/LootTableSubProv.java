package net.sterbendes.greeneries.neoforge.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record LootTableSubProv(HolderLookup.Provider provider) implements LootTableSubProvider {

    public static final Stream<ResourceKey<Block>> dropSeedsOrSelf = Stream.of(
        "very_short_common_bent",
        "short_common_bent",
        "bushy_common_bent",
        "very_short_red_fescue",
        "short_red_fescue",
        "bushy_red_fescue",
        "medium_red_fescue",
        "very_short_blue_grass",
        "short_blue_grass",
        "bushy_blue_grass"
    ).map(s -> ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("greeneries", s)));


    @Override
    public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        dropSeedsOrSelf.forEach(blockKey ->
            consumer.accept(ResourceKey.create(Registries.LOOT_TABLE, blockKey.location().withPrefix("blocks/")),
                LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                        .add(
                            AlternativesEntry.alternatives(
                                LootItem.lootTableItem(lookupBlock(blockKey))
                                    .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, Items.SHEARS))),
                                LootItem.lootTableItem(Items.WHEAT_SEEDS)
                                    .when(LootItemRandomChanceCondition.randomChance(0.125f))
                                    .apply(ApplyBonusCount.addUniformBonusCount(getFortuneEnchantHolder(), 2))
                                    .apply(ApplyExplosionDecay.explosionDecay())
                            )
                        )
                    )
            )
        );
    }

    private Block lookupBlock(ResourceKey<Block> blockKey) {
        return provider.lookupOrThrow(Registries.BLOCK).get(blockKey).orElseThrow().value();
    }

    private Holder<Enchantment> getFortuneEnchantHolder() {
        return provider.lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FORTUNE).orElseThrow();
    }
}
