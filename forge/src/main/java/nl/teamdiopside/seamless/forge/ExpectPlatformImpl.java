package nl.teamdiopside.seamless.forge;

import net.minecraftforge.fml.loading.FMLPaths;
import nl.teamdiopside.seamless.ExpectPlatform;

import java.nio.file.Path;

public class ExpectPlatformImpl {
    /**
     * This is our actual method to {@link ExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
