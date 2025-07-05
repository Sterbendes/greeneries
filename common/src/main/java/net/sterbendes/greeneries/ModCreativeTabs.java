package net.sterbendes.greeneries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static net.sterbendes.greeneries.GreeneriesMod.platform;

public abstract class ModCreativeTabs {

    public static final Holder<CreativeModeTab> vd_extra_tab = GreeneriesMod.register(
        "vd_extra_tab", BuiltInRegistries.CREATIVE_MODE_TAB,
        () -> platform.creativeTabBuilder()
            .icon(() -> new ItemStack(Blocks.SHORT_GRASS))
            .title(Component.literal("Vegan Delight Extra"))
            .displayItems((params, output) -> {
                for (ItemLike item : getAllGreeneriesItems()) output.accept(item);
            }).build()
    );

    @Contract(" -> new")
    public static ItemLike @NotNull [] getAllGreeneriesItems() {
        return new ItemLike[]{ };
    }

    static void init() { }
}
