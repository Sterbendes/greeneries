package net.sterbendes.greeneries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.sterbendes.greeneries.blocks.ModBlocks;
import org.jetbrains.annotations.Contract;

import static net.sterbendes.greeneries.GreeneriesMod.platform;

@SuppressWarnings("unused")
public abstract class ModCreativeTabs {

    public static final Holder<CreativeModeTab> greeneries_tab = GreeneriesMod.register(
        "greeneries_tab", BuiltInRegistries.CREATIVE_MODE_TAB,
        () -> platform.creativeTabBuilder()
            .icon(() -> new ItemStack(ModBlocks.get("short_red_fescue").value()))
            .title(Component.literal("Greeneries"))
            .displayItems((params, output) -> {
                for (ItemLike item : getAllGreeneriesItems()) {
                    var location = ResourceLocation.parse(item.asItem().toString());
                    if (ModBlocks.SMALL_FLOWERS.stream().anyMatch(blockHolder -> blockHolder.is(location)))
                        continue;
                    if (ModBlocks.VERY_SMALL_FLOWERS.stream().anyMatch(blockHolder -> blockHolder.is(location)))
                        continue;

                    output.accept(item);
                }
            }).build()
    );

    public static final Holder<CreativeModeTab> flower_tab = GreeneriesMod.register(
        "flowers_tab", BuiltInRegistries.CREATIVE_MODE_TAB,
        () -> platform.creativeTabBuilder()
            .icon(() -> new ItemStack(Blocks.POPPY))
            .title(Component.translatable("itemGroup.flowers"))
            .displayItems((params, output) -> {
                for (int i = 0; i < ModBlocks.FLOWERS.size(); i++) {
                    output.accept(ModBlocks.FLOWERS.get(i));
                    output.accept(ModBlocks.SMALL_FLOWERS.get(i).value());
                    output.accept(ModBlocks.VERY_SMALL_FLOWERS.get(i).value());
                }
            }).build()
    );

    @Contract(" -> new")
    public static ItemLike[] getAllGreeneriesItems() {
        return ModBlocks.getAllGreeneriesBlocks().stream().map(Holder::value).toArray(ItemLike[]::new);
    }

    @SuppressWarnings("EmptyMethod")
    static void init() { }
}
