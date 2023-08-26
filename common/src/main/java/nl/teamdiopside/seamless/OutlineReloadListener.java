package nl.teamdiopside.seamless;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class OutlineReloadListener extends SimpleJsonResourceReloadListener {

    public OutlineReloadListener() {
        super(new Gson(), "outline_rules");
    }

    public record OutlineRule(Set<Block> blocks, HashMap<String, Set<String>> blockstates, Set<Direction> directions, Set<Block> connectingBlocks, HashMap<String, Set<String>> connectingBlockstates) {}

    public static final List<OutlineRule> RULES = new ArrayList<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        RULES.clear();
        List<OutlineRule> temp = new ArrayList<>();
        jsons.forEach((key, json) -> {
            try {
                Set<Block> blocks = new HashSet<>();
                json.getAsJsonObject().get("blocks").getAsJsonArray().forEach(jsonElement -> {
                    if (jsonElement.getAsString().startsWith("#")) {
                        TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(jsonElement.getAsString().replace("#", "")));
                        BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder -> blocks.add(blockHolder.value()));
                    } else {
                        blocks.add(BuiltInRegistries.BLOCK.get(new ResourceLocation(jsonElement.getAsString())));
                    }
                });

                HashMap<String, Set<String>> blockstates = new HashMap<>();
                json.getAsJsonObject().get("blockstates").getAsJsonObject().asMap().forEach((k, v) -> {
                    Set<String> states = new HashSet<>();
                    v.getAsJsonArray().forEach(jsonElement -> states.add(jsonElement.getAsString()));
                    blockstates.put(k, states);
                });

                Set<Direction> directions = new HashSet<>();
                json.getAsJsonObject().get("directions").getAsJsonArray().forEach(jsonElement ->
                        directions.add(Direction.byName(jsonElement.getAsString()))
                );

                Set<Block> connectingBlocks = new HashSet<>();
                json.getAsJsonObject().get("connecting_blocks").getAsJsonArray().forEach(jsonElement -> {
                    if (jsonElement.getAsString().startsWith("#")) {
                        TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(jsonElement.getAsString().replace("#", "")));
                        BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder ->
                                connectingBlocks.add(blockHolder.value())
                        );
                    } else {
                        connectingBlocks.add(BuiltInRegistries.BLOCK.get(new ResourceLocation(jsonElement.getAsString())));
                    }
                });

                HashMap<String, Set<String>> connectingBlockstates = new HashMap<>();
                json.getAsJsonObject().get("connecting_blockstates").getAsJsonObject().asMap().forEach((k, v) -> {
                    Set<String> states = new HashSet<>();
                    v.getAsJsonArray().forEach(jsonElement -> states.add(jsonElement.getAsString()));
                    connectingBlockstates.put(k, states);
                });

                temp.add(new OutlineRule(blocks, blockstates, directions, connectingBlocks, connectingBlockstates));
                Seamless.LOGGER.info("Found outline rule " + key.getPath() + ".json");
            } catch (Exception e) {
                Seamless.LOGGER.error("Failed to parse JSON object for outline rule " + key + ", Cause: " + e);
            }
        });

        RULES.addAll(temp);
    }

    public static Set<Block> getBlocks(JsonElement json, String key) {
        Set<Block> blocks = new HashSet<>();
        json.getAsJsonObject().get(key).getAsJsonArray().forEach(jsonElement -> {
            if (jsonElement.getAsString().startsWith("#")) {
                TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(jsonElement.getAsString().replace("#", "")));
                BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder -> blocks.add(blockHolder.value()));
            } else {
                blocks.add(BuiltInRegistries.BLOCK.get(new ResourceLocation(jsonElement.getAsString())));
            }
        });
        return blocks;
    }

    public static HashMap<String, Set<String>> getBlockStates(JsonElement json, String key) {
        HashMap<String, Set<String>> blockstates = new HashMap<>();
        json.getAsJsonObject().get(key).getAsJsonObject().asMap().forEach((k, v) -> {
            Set<String> states = new HashSet<>();
            v.getAsJsonArray().forEach(jsonElement -> states.add(jsonElement.getAsString()));
            blockstates.put(k, states);
        });
        return blockstates;
    }

    public static Set<Direction> getDirections(JsonElement json, String key) {
        Set<Direction> directions = new HashSet<>();
        json.getAsJsonObject().get(key).getAsJsonArray().forEach(jsonElement ->
                directions.add(Direction.byName(jsonElement.getAsString()))
        );
        return directions;
    }
}
