package nl.teamdiopside.seamless.mixin;

import net.minecraft.client.Option;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import nl.teamdiopside.seamless.Seamless;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin {
    @Shadow private OptionsList list;

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        list.addSmall(new Option[]{Seamless.fastOption});
    }
}
