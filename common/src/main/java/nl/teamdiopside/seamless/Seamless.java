package nl.teamdiopside.seamless;

import dev.architectury.platform.Platform;
import net.minecraft.client.CycleOption;
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
    public static CycleOption<Boolean> fastOption = CycleOption.createOnOff("options.fast_seamless", (options) -> fastEnabled, (options, option, object) -> fastEnabled = object);

    public static void init() {
        modIds = Platform.getModIds().stream().toList();
    }
}
