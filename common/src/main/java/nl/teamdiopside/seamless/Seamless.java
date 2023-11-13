package nl.teamdiopside.seamless;

import dev.architectury.platform.Platform;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Seamless {
    public static final String MOD_ID = "seamless";
    public static final String RESOURCE_PACK = "default_seamless";
    public static final Logger LOGGER = LoggerFactory.getLogger("Seamless");
    public static List<String> modIds = new ArrayList<>();

    public static boolean fastEnabled = false;
    public static OptionInstance<Boolean> fastOption = OptionInstance.createBoolean("options.fast_seamless", OptionInstance.cachedConstantTooltip(Component.translatable("options.fast_seamless.tooltip")), false, aBoolean -> fastEnabled = aBoolean);

    public static void init() {
        modIds = Platform.getModIds().stream().toList();
        fastEnabled = fastOption.get();
    }
}
