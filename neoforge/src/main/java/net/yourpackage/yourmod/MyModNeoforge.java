package net.yourpackage.yourmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MyMod.modID)
public class MyModNeoforge {

    public static IEventBus modEventBus;

    public MyModNeoforge(IEventBus modEventBus) {
        MyModNeoforge.modEventBus = modEventBus;

        MyMod.init();
        // Your neoforge initialisation code here
    }
}
