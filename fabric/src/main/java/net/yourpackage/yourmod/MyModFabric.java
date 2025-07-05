package net.yourpackage.yourmod;

import net.fabricmc.api.ModInitializer;

public class MyModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MyMod.init();
        // Your fabric initialisation code here
    }
}
