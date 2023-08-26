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
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class OutlineReloadListener extends SimpleJsonResourceReloadListener {

    public OutlineReloadListener() {
        super(new Gson(), "seamless_rules");
    }

    public record OutlineRule(Set<Block> blocks, HashMap<String, Set<String>> blockstates, Set<Direction> directions, Set<Block> connectingBlocks, HashMap<String, Set<String>> connectingBlockstates) {}

    public static final List<OutlineRule> RULES = new ArrayList<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        RULES.clear();
        List<OutlineRule> temp = new ArrayList<>();
        jsons.forEach((key, json) -> {
//            if (!Platform.getModIds().contains(key.getNamespace())) {
//                return;
//            }
            try {
                Set<Block> blocks = getBlocks(key, json, "blocks");
                HashMap<String, Set<String>> blockstates = getBlockStates(json, "blockstates");
                Set<Direction> directions = getDirections(key, json, "directions");
                Set<Block> connectingBlocks = getBlocks(key, json, "connecting_blocks");
                HashMap<String, Set<String>> connectingBlockstates = getBlockStates(json, "connecting_blockstates");

                temp.add(new OutlineRule(blocks, blockstates, directions, connectingBlocks, connectingBlockstates));
                Seamless.LOGGER.info("Found outline rule " + key);
            } catch (Exception e) {
                Seamless.LOGGER.error("Failed to parse JSON object for outline rule " + key + ".json, Error: " + e);
            }
        });

        RULES.addAll(temp);
    }

    public static Set<Block> getBlocks(ResourceLocation key, JsonElement json, String string) {
        Set<Block> blocks = new HashSet<>();
        json.getAsJsonObject().get(string).getAsJsonArray().forEach(jsonElement -> {
            if (jsonElement.getAsString().startsWith("#")) {
                TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(jsonElement.getAsString().replace("#", "")));
                BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder -> blocks.add(blockHolder.value()));
            } else {
                Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(jsonElement.getAsString()));
                if (block == Blocks.AIR && !jsonElement.getAsString().replace("minecraft:", "").equals("air")) {
                    Seamless.LOGGER.error("Block \"" + jsonElement.getAsString() + "\" from " + key + " does not exist!");
                } else {
                    blocks.add(block);
                }
            }
        });
        return blocks;
    }

    public static HashMap<String, Set<String>> getBlockStates(JsonElement json, String string) {
        HashMap<String, Set<String>> blockstates = new HashMap<>();
        try {
            json.getAsJsonObject().get(string).getAsJsonObject().asMap().forEach((k, v) -> {
                Set<String> states = new HashSet<>();
                v.getAsJsonArray().forEach(jsonElement -> states.add(jsonElement.getAsString()));
                blockstates.put(k, states);
            });
        } catch (NullPointerException ignored) {

        }
        return blockstates;
    }

    public static Set<Direction> getDirections(ResourceLocation key, JsonElement json, String string) {
        Set<Direction> directions = new HashSet<>();
        json.getAsJsonObject().get(string).getAsJsonArray().forEach(jsonElement -> {
            Direction direction = Direction.byName(jsonElement.getAsString());
            if (direction != null) {
                directions.add(direction);
            } else {
                Seamless.LOGGER.error("Direction \"" + jsonElement.getAsString() + "\" from " + key + " does not exist!");
            }
        });
        return directions;
    }
}
