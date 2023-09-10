package nl.teamdiopside.seamless.fabric;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SeamlessFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Seamless.init();

        ModContainer container = FabricLoader.getInstance().getModContainer("seamless").orElseThrow();
        ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("seamless", "default_seamless"), container, Component.translatable("seamless.resource_pack"), ResourcePackActivationType.DEFAULT_ENABLED);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("seamless", "outline_reload_listener");
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
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
