package nl.teamdiopside.seamless.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SeamlessFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Seamless.init();

        ModContainer container = FabricLoader.getInstance().getModContainer(Seamless.MOD_ID).orElseThrow();
        ResourceManagerHelper.registerBuiltinResourcePack(ResourceLocation.fromNamespaceAndPath(Seamless.MOD_ID, Seamless.RESOURCE_PACK), container, Component.translatable("seamless.resource_pack"), ResourcePackActivationType.DEFAULT_ENABLED);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
                return new Reload.OutlineRuleReloadListener() {}.reload(preparationBarrier, resourceManager, executor, executor2);
            }

            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(Seamless.MOD_ID, "outline_reload_listener");
            }
        });
    }
}
