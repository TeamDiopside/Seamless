package nl.teamdiopside.seamless;

import dev.architectury.platform.Platform;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Seamless {
    public static final String MOD_ID = "seamless";
    public static final String RESOURCE_PACK = "default_seamless";
    public static final Logger LOGGER = LoggerFactory.getLogger("Seamless");
    public static List<String> modIds = new ArrayList<>();
    public static Set<String> errors = new HashSet<>();

    public static final Component FAST_SEAMLESS_TEXT = Component.translatable("options.fast_seamless");
    public static final Component FAST_SEAMLESS_TOOLTIP = Component.translatable("options.fast_seamless.tooltip");

    public static boolean fastEnabled = false;
    public static OptionInstance<Boolean> fastOption = OptionInstance.createBoolean("options.fast_seamless", OptionInstance.cachedConstantTooltip(FAST_SEAMLESS_TOOLTIP), false, aBoolean -> fastEnabled = aBoolean);

    public static void init() {
        modIds = Platform.getModIds().stream().toList();
    }
}
