package nl.teamdiopside.seamless.fabric;

import net.fabricmc.api.ModInitializer;
import nl.teamdiopside.seamless.Seamless;

public class SeamlessFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Seamless.init();
    }
}
