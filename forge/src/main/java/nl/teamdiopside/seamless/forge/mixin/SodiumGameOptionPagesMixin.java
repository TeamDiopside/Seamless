package nl.teamdiopside.seamless.forge.mixin;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.binding.compat.VanillaBooleanOptionBinding;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import nl.teamdiopside.seamless.Seamless;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;

@Mixin(SodiumGameOptionPages.class)
public abstract class SodiumGameOptionPagesMixin {
    @Shadow @Final private static MinecraftOptionsStorage vanillaOpts;

    @ModifyArg(method = "general", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"), remap = false)
    private static Collection<OptionGroup> addOption(Collection<OptionGroup> groups) {
        groups.add(OptionGroup.createBuilder()
                .add(OptionImpl.createBuilder(Boolean.TYPE, vanillaOpts)
                        .setName(Seamless.FAST_SEAMLESS_TEXT)
                        .setTooltip(Seamless.FAST_SEAMLESS_TOOLTIP)
                        .setControl(TickBoxControl::new)
                        .setBinding(new VanillaBooleanOptionBinding(Seamless.fastOption)).build())
                .build());
        return groups;
    }
}
