package nl.curryducker.seamless.forge;

import dev.architectury.platform.forge.EventBuses;
import nl.curryducker.seamless.Seamless;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Seamless.MOD_ID)
public class SeamlessForge {
    public SeamlessForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Seamless.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Seamless.init();
    }
}
