package nl.teamdiopside.seamless.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.teamdiopside.seamless.Seamless;

@Mod(Seamless.MOD_ID)
public class SeamlessForge {

    public SeamlessForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Seamless.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SeamlessForgeClient::init);
        Seamless.init();
    }
}
