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
        EventBuses.registerModEventBus("seamless", FMLJavaModLoadingContext.get().getModEventBus());

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SeamlessForgeClient::init);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Seamless::init);
    }
}
