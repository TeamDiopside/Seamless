package nl.curryducker.seamless;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Outline extends SimpleJsonResourceReloadListener {

    public Outline() {
        super(new Gson(), "outline_rules");
    }

    public static record OutlineRule(Block self, RuleTest selfTest, Map<Direction, RuleTest> targets) {
    }

    public static final Codec<OutlineRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("self").forGetter(OutlineRule::self),
            RuleTest.CODEC.fieldOf("self_test").forGetter(OutlineRule::selfTest),
            Codec.simpleMap(Direction.CODEC, RuleTest.CODEC, StringRepresentable.keys(Direction.values())).fieldOf("targets").forGetter(OutlineRule::targets)
    ).apply(instance, OutlineRule::new));

    public static final Map<Block, List<OutlineRule>> RULES = new HashMap<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {

        RULES.clear();
        List<OutlineRule> temp = new ArrayList<>();
        jsons.forEach((key, json) -> {
            try {
                var result = CODEC.parse(JsonOps.INSTANCE, json);
                OutlineRule rule = result.getOrThrow(false, e -> Seamless.LOGGER.error("Failed to parse outline rule: {}", e));
                temp.add(rule);
            } catch (Exception e) {
                Seamless.LOGGER.error("Failed to parse JSON object for outline rule " + key);
            }
        });

        temp.forEach(t -> RULES.computeIfAbsent(t.self, g -> new ArrayList<>()).add(t));
    }

    public static final RandomSource rs = RandomSource.create();
}
