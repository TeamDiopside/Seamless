package nl.teamdiopside.seamless;

import dev.architectury.platform.Platform;
import net.minecraft.client.CycleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

    public static final Component FAST_SEAMLESS_TEXT = new TranslatableComponent("options.fast_seamless");
    public static final Component FAST_SEAMLESS_TOOLTIP = new TranslatableComponent("options.fast_seamless.tooltip");

    public static boolean fastEnabled = false;
    public static CycleOption<Boolean> fastOption = CycleOption.createOnOff("options.fast_seamless", (options) -> fastEnabled, (options, option, object) -> fastEnabled = object);

    public static void init() {
        modIds = Platform.getModIds().stream().toList();
    }
}
