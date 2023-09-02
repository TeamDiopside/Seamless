package nl.teamdiopside.seamless.fabric;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SeamlessFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Seamless.init();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("seamless", "outline_reload_listener");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return new SimpleJsonResourceReloadListener(new Gson(), "nothing") {
                    @Override
                    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                        reloadOutlines();
                    }
                }.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }
        });

        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client) {
                reloadOutlines();
            }
        });
    }

    public static void reloadOutlines() {
        Reload.reload(Minecraft.getInstance().getResourceManager());
    }
}
