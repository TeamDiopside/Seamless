package nl.teamdiopside.seamless.fabric;

import net.fabricmc.loader.api.FabricLoader;
import nl.teamdiopside.seamless.ExpectPlatform;

import java.nio.file.Path;

public class ExpectPlatformImpl {
    /**
     * This is our actual method to {@link ExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
