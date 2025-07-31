package net.sterbendes.greeneries.data;

import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class DataGenerator {

    public static void onGatherData(@NotNull GatherDataEvent event) {
        var registries = event.getLookupProvider();
        event.getGenerator().addProvider(
            true,
            (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootTableSubProv::new, LootContextParamSets.BLOCK)
            ), registries)
        );
    }
}
