package nl.teamdiopside.seamless.forge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;

import java.util.Map;
import java.util.function.Consumer;

@Mod(Seamless.MOD_ID)
public class SeamlessForge {

    public SeamlessForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Seamless.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Seamless.init();

        Consumer<AddReloadListenerEvent> reloadListener = addReloadListenerEvent -> addReloadListenerEvent.addListener(new SimpleJsonResourceReloadListener(new Gson(), "nothing") {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager arg, ProfilerFiller arg2) {
                reloadOutlines();
            }
        });

        Consumer<TagsUpdatedEvent> tags = tagsUpdatedEvent -> {
            if (tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
                reloadOutlines();
            }
        };

        MinecraftForge.EVENT_BUS.addListener(reloadListener);
        MinecraftForge.EVENT_BUS.addListener(tags);
    }

    public static void reloadOutlines() {
        Reload.reload(Minecraft.getInstance().getResourceManager());
    }
}
