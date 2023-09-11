package nl.teamdiopside.seamless.forge.mixin;

import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import nl.teamdiopside.seamless.Seamless;
import nl.teamdiopside.seamless.forge.SeamlessForge;
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

    @Inject(method = "load()V", at = @At("RETURN"))
    private void update(CallbackInfo ci) {
        if (!SeamlessForge.file.exists() && !resourcePacks.contains(Seamless.RESOURCE_PACK)) {
            resourcePacks.add(Seamless.RESOURCE_PACK);
        }
    }

    @Inject(method = "updateResourcePacks", at = @At("HEAD"))
    private void update(PackRepository arg, CallbackInfo ci) {
        for (Pack pack : arg.getSelectedPacks()) {
            if (pack.getId().equals(Seamless.RESOURCE_PACK)) {
                SeamlessForge.file.delete();
            } else {
                try {
                    SeamlessForge.file.createNewFile();
                    FileWriter writer = new FileWriter(SeamlessForge.file);
                    writer.write("Don't delete this file if you want to disable the default Seamless resources, otherwise it's ok to delete :)");
                    writer.close();
                }
                catch (IOException ignored) {}
            }
        }
    }
}
