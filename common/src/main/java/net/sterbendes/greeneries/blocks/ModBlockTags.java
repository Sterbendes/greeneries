package net.sterbendes.greeneries.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.sterbendes.greeneries.GreeneriesMod;

public class ModBlockTags {

    public static final TagKey<Block> reed_may_place_on = get("reed_may_place_on");

    private static TagKey<Block> get(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GreeneriesMod.modID, name));
    }
}
