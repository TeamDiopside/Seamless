package nl.teamdiopside.seamless;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutlineReloadListener extends SimpleJsonResourceReloadListener {

    public OutlineReloadListener() {
        super(new Gson(), "outline_rules");
    }

    public record OutlineRule(List<Block> blocks, HashMap<String, List<String>> blockstates, List<Direction> directions, List<Block> connectingBlocks, HashMap<String, List<String>> connectingBlockstates) {
    }

    public static final List<OutlineRule> RULES = new ArrayList<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        RULES.clear();
        List<OutlineRule> temp = new ArrayList<>();
        jsons.forEach((key, json) -> {
            try {
                List<Block> blocks = new ArrayList<>();
                json.getAsJsonObject().get("blocks").getAsJsonArray().forEach(jsonElement ->
                        blocks.add(BuiltInRegistries.BLOCK.get(new ResourceLocation(jsonElement.getAsString())))
                );

                HashMap<String, List<String>> blockstates = new HashMap<>();
                json.getAsJsonObject().get("blockstates").getAsJsonObject().asMap().forEach((k, v) -> {
                    List<String> states = new ArrayList<>();
                    v.getAsJsonArray().forEach(jsonElement -> states.add(jsonElement.getAsString()));
                    blockstates.put(k, states);
                });

                List<Direction> directions = new ArrayList<>();
                json.getAsJsonObject().get("directions").getAsJsonArray().forEach(jsonElement ->
                        directions.add(Direction.byName(jsonElement.getAsString()))
                );

                List<Block> connectingBlocks = new ArrayList<>();
                json.getAsJsonObject().get("connecting_blocks").getAsJsonArray().forEach(jsonElement ->
                        connectingBlocks.add(BuiltInRegistries.BLOCK.get(new ResourceLocation(jsonElement.getAsString())))
                );

                HashMap<String, List<String>> connectingBlockstates = new HashMap<>();
                json.getAsJsonObject().get("connecting_blockstates").getAsJsonObject().asMap().forEach((k, v) -> {
                    List<String> states = new ArrayList<>();
                    v.getAsJsonArray().forEach(jsonElement -> states.add(jsonElement.getAsString()));
                    connectingBlockstates.put(k, states);
                });

                temp.add(new OutlineRule(blocks, blockstates, directions, connectingBlocks, connectingBlockstates));

                Seamless.LOGGER.info("Found outline rule " + key.getPath());

            } catch (Exception e) {
                Seamless.LOGGER.error("Failed to parse JSON object for outline rule " + key + ", Cause: " + e);
            }
        });

        RULES.addAll(temp);
    }

    public static final RandomSource rs = RandomSource.create();
}
