package nl.teamdiopside.seamless.forge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.locating.IModFile;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class SeamlessForgeClient {

    public static File file = new File(Minecraft.getInstance().gameDirectory, "seamless.txt");

    public static void init() {
        Consumer<AddPackFindersEvent> packFinders = SeamlessForgeClient::addPack;

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(packFinders);

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

    public static void addPack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            try {
                ResourceLocation folderName = new ResourceLocation(Seamless.MOD_ID, Seamless.RESOURCE_PACK);
                IModFile file = ModList.get().getModFileById(folderName.getNamespace()).getFile();
                PathPackResources pack = new PathPackResources(folderName.toString(), file.findResource("resourcepacks/" + folderName.getPath()), true);
                var metadata = Objects.requireNonNull(pack.getMetadataSection(PackMetadataSection.TYPE));
                event.addRepositorySource(consumer -> consumer.accept(Pack.create(
                        folderName.getPath(),
                        Component.translatable("seamless.resource_pack"),
                        false,
                        new Pack.ResourcesSupplier() {
                            @Override
                            public PackResources openPrimary(String string) {
                                return pack;
                            }

                            @Override
                            public PackResources openFull(String string, Pack.Info arg) {
                                return pack;
                            }
                        },
                        new Pack.Info(metadata.description(), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), new ArrayList<>(), false),
                        Pack.Position.TOP,
                        false,
                        PackSource.BUILT_IN
                )));
            } catch (Exception e) {
                Seamless.LOGGER.error("Failed to add Built-in Seamless resources");
            }
        }
    }
}
