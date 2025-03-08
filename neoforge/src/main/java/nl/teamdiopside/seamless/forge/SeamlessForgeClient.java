package nl.teamdiopside.seamless.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;

import java.io.File;

@Mod(value = Seamless.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Seamless.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SeamlessForgeClient {

    public static File file = new File(Minecraft.getInstance().gameDirectory, "seamless.txt");

    public SeamlessForgeClient() {

//        NeoForge.EVENT_BUS.register(SeamlessForgeClientModEvents.class);

        Seamless.init();
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(Seamless.MOD_ID, "outline_reload_listener"), new Reload.OutlineRuleReloadListener());
    }

}

