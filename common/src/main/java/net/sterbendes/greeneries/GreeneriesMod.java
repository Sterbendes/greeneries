package net.sterbendes.greeneries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Supplier;

public class GreeneriesMod {

    static final String modID = "greeneries";
    @UnknownNullability
    static GreeneriesPlatform platform;

    static void init(GreeneriesPlatform platform) {
        System.out.println("Hi from example mod!");

        GreeneriesMod.platform = platform;
        // Your common initialisation code here
    }

    static <T> Holder<T> register(String name, Registry<T> registry, Supplier<T> obj) {
        return platform.register(registry, ResourceLocation.fromNamespaceAndPath(modID, name), obj);
    }
}
