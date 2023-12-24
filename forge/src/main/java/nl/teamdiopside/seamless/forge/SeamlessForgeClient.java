package nl.teamdiopside.seamless.forge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import nl.teamdiopside.seamless.Reload;

import java.util.Map;
import java.util.function.Consumer;

public class SeamlessForgeClient {

    public static void init() {
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
