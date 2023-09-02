package nl.teamdiopside.seamless.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.teamdiopside.seamless.OutlineReloadListener;
import nl.teamdiopside.seamless.Seamless;

import java.util.function.Consumer;

@Mod(Seamless.MOD_ID)
public class SeamlessForge {
    public SeamlessForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Seamless.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Seamless.init();
        Consumer<AddReloadListenerEvent> eventConsumer = addReloadListenerEvent -> addReloadListenerEvent.addListener(new OutlineReloadListener());
        MinecraftForge.EVENT_BUS.addListener(eventConsumer);
    }
}
