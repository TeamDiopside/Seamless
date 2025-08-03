package nl.teamdiopside.seamless.forge.mixin;

import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import nl.teamdiopside.seamless.Seamless;
import nl.teamdiopside.seamless.forge.SeamlessForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Shadow public List<String> resourcePacks;

    @Inject(method = "load", at = @At("RETURN"))
    private void update(CallbackInfo ci) {
        if (!SeamlessForgeClient.getFile().exists() && !resourcePacks.contains(Seamless.FORGE_RESOURCE_PACK_ID)) {
            resourcePacks.add(Seamless.FORGE_RESOURCE_PACK_ID);
        }
    }

    @Inject(method = "updateResourcePacks", at = @At("HEAD"))
    private void update(PackRepository arg, CallbackInfo ci) {
        // Pack is selected
        if (arg.getSelectedIds().stream().anyMatch(s -> s.equals(Seamless.FORGE_RESOURCE_PACK_ID))) {
            boolean deleted = SeamlessForgeClient.getFile().delete();
            if (!deleted) Seamless.LOGGER.error("Could not delete file");
            return;
        }

        // Pack is not selected
        try {
            if (!SeamlessForgeClient.getFile().createNewFile()) {
                throw new IOException();
            }
            FileWriter writer = new FileWriter(SeamlessForgeClient.getFile());
            writer.write("If this file is present, the built in seamless resource pack will be automatically disabled\n\nhttps://www.youtube.com/watch?v=IFfLCuHSZ-U");
            writer.close();
        } catch (IOException e) {
            Seamless.LOGGER.error("Could not create file", e);
        }
    }
}
