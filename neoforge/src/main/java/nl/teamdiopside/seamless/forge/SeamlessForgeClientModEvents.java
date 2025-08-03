package nl.teamdiopside.seamless.forge;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import nl.teamdiopside.seamless.Seamless;

@EventBusSubscriber(modid = Seamless.MOD_ID)
public class SeamlessForgeClientModEvents {

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {

        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Seamless.MOD_ID, Seamless.RESOURCE_PACK_PATH),
                PackType.CLIENT_RESOURCES,
                Component.translatable("seamless.resource_pack"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );
    }
}
