package net.sterbendes.greeneries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Contract;

import static net.sterbendes.greeneries.GreeneriesMod.platform;

public abstract class ModCreativeTabs {

    public static final Holder<CreativeModeTab> greeneries_tab = GreeneriesMod.register(
        "greeneries_tab", BuiltInRegistries.CREATIVE_MODE_TAB,
        () -> platform.creativeTabBuilder()
            .icon(() -> new ItemStack(ModBlocks.get("short_red_fescue").value()))
            .title(Component.literal("Greeneries"))
            .displayItems((params, output) -> {
                for (ItemLike item : getAllGreeneriesItems()) output.accept(item);
            }).build()
    );

    @Contract(" -> new")
    public static ItemLike[] getAllGreeneriesItems() {
        return ModBlocks.getAllGreeneriesBlocks().stream().map(Holder::value).toArray(ItemLike[]::new);
    }

    static void init() { }
}
