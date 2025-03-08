package nl.teamdiopside.seamless;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Reload {

    public static final Map<ResourceLocation, OutlineRule> RULES = new ConcurrentHashMap<>();

    public record OutlineRule(Set<String> blocks, Optional<Map<String, Set<String>>> blockstates, Set<String> directions, Set<String> connectingBlocks, Optional<Map<String, Set<String>>> connectingBlockstates) {
        public static final Codec<OutlineRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("blocks").forGetter(OutlineRule::blocks),
                Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf)).optionalFieldOf("blockstates").forGetter(OutlineRule::blockstates),
                Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("directions").forGetter(OutlineRule::directions),
                Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("connecting_blocks").forGetter(OutlineRule::connectingBlocks),
                Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf)).optionalFieldOf("connecting_blockstates").forGetter(OutlineRule::connectingBlockstates)
        ).apply(instance, OutlineRule::new));
    }

    public static class OutlineRuleReloadListener extends SimpleJsonResourceReloadListener<OutlineRule> {

        public OutlineRuleReloadListener() {
            super(OutlineRule.CODEC, FileToIdConverter.json("seamless_rules"));
        }

        @Override
        protected void apply(Map<ResourceLocation, OutlineRule> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            Seamless.fastEnabled = Seamless.fastOption.get();
            Reload.RULES.clear();
            Reload.RULES.putAll(map);
            Seamless.LOGGER.info("Loaded {} Seamless outline rules", map.size());
        }
    }
}
