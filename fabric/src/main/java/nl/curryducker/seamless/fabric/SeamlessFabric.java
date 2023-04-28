package nl.curryducker.seamless.fabric;

import nl.curryducker.seamless.Seamless;
import net.fabricmc.api.ModInitializer;

public class SeamlessFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Seamless.init();
    }
}
