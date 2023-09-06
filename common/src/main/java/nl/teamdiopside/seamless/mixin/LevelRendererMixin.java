package nl.teamdiopside.seamless.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.Recursion;
import nl.teamdiopside.seamless.Reload;
import nl.teamdiopside.seamless.Seamless;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static nl.teamdiopside.seamless.Reload.RULES;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {

    @Shadow private @Nullable ClientLevel level;

    @Shadow public abstract void setLevel(@Nullable ClientLevel clientLevel);

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double i, double b, double c, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        Recursion recursion = findAndAddShapes(level, blockState, blockPos, new HashSet<>(), blockPos, entity);
        VoxelShape shape = recursion.voxelShape().optimize();

        LevelRenderer.renderShape(poseStack, vertexConsumer, shape, (double)blockPos.getX() - i, (double)blockPos.getY() - b, (double)blockPos.getZ() - c, 0.0f, 0.0f, 0.0f, 0.4f);
        ci.cancel();
    }

    @Unique
    private static Recursion findAndAddShapes(Level level, BlockState state, BlockPos pos, Set<BlockPos> connectedPositions, BlockPos originalPos, Entity entity) {
        connectedPositions.add(pos);
        BlockPos relativePos = pos.subtract(originalPos);
        VoxelShape shape = state.getShape(level, pos, CollisionContext.of(entity)).move(relativePos.getX(), relativePos.getY(), relativePos.getZ());

        if (connectedPositions.size() > 2000) {
            return new Recursion(shape, connectedPositions);
        }

        for (Reload.OutlineRule outlineRule : RULES) {
            if (!getBlocks(outlineRule.blocks(), null, outlineRule.location()).contains(state.getBlock())) {
                continue;
            }

            boolean blockstatesMatch = true;
            for (HashMap.Entry<String, Set<String>> entry : outlineRule.blockstates().entrySet()) {
                if (!propertyMatches(state, entry.getKey(), entry.getValue(), null)) {
                    blockstatesMatch = false;
                }
            }
            if (!blockstatesMatch) {
                continue;
            }

            for (Direction direction : getDirections(outlineRule.directions(), outlineRule.location())) {
                BlockPos checkingPos = pos.relative(direction);
                BlockState checkingState = level.getBlockState(checkingPos);

                if (connectedPositions.contains(checkingPos) || !getBlocks(outlineRule.connectingBlocks(), state, outlineRule.location()).contains(checkingState.getBlock())) {
                    continue;
                }

                boolean connectingBlockstatesMatch = true;
                for (HashMap.Entry<String, Set<String>> entry : outlineRule.connectingBlockstates().entrySet()) {
                    if (!propertyMatches(checkingState, entry.getKey(), entry.getValue(), state)) {
                        connectingBlockstatesMatch = false;
                        break;
                    }
                }
                if (!connectingBlockstatesMatch) {
                    continue;
                }

                Recursion recursion = findAndAddShapes(level, checkingState, checkingPos, connectedPositions, originalPos, entity);
                shape = Shapes.joinUnoptimized(shape, recursion.voxelShape(), BooleanOp.OR);
                connectedPositions = recursion.connectedPositions();
            }
        }
        return new Recursion(shape, connectedPositions);
    }

    @Unique
    private static Set<Direction> getDirections(Set<String> set, ResourceLocation location) {
        Set<Direction> directions = new HashSet<>();
        for (String string : set) {
            Direction direction = Direction.byName(string);
            if (direction != null) {
                directions.add(direction);
            } else {
                Seamless.LOGGER.error("Direction \"" + string + "\" from " + location + " does not exist!");
            }
        }
        return directions;
    }

    @Unique
    private static Set<Block> getBlocks(Set<String> set, BlockState state, ResourceLocation location) {
        Set<Block> blocks = new HashSet<>();

        for (String string : set) {
            if (string.startsWith("/same") && state != null) {
                blocks.add(state.getBlock());
            } else {
                blocks.addAll(getNormalBlocks(string, location));
            }
        }

        return blocks;
    }

    @Unique
    private static Set<Block> getNormalBlocks(String string, ResourceLocation location) {
        Set<Block> blocks = new HashSet<>();
        if (string.startsWith("#")) {
            TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(string.replace("#", "")));
            BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder -> blocks.add(blockHolder.value()));
        } else {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(string));
            if (block == Blocks.AIR && !string.replace("minecraft:", "").equals("air")) {
                Seamless.LOGGER.error("Block \"" + string + "\" from " + location + " does not exist!");
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    @Unique
    private static boolean propertyMatches(BlockState checkingState, String propertyName, Set<String> values, BlockState originalState) {
        Property<?> checkingProperty = checkingState.getBlock().getStateDefinition().getProperty(propertyName);
        assert checkingProperty != null : "Blockstate property " + propertyName + " does not exist for " + checkingState.getBlock().getName();

        String valueName = checkingState.getValue(checkingProperty).toString();
        if (checkingState.getValue(checkingProperty) instanceof StringRepresentable representable) {
            valueName = representable.getSerializedName();
        }

        boolean propertiesMatch = propertyName.equals(checkingProperty.getName());
        boolean valuesMatch = values.contains(valueName);

        if (originalState != null) {
            for (String value : values) {
                if (value.startsWith("/same")) {
                    Property<?> originalProperty = originalState.getBlock().getStateDefinition().getProperty(propertyName);
                    assert originalProperty != null : "Blockstate property " + propertyName + " does not exist for " + originalState.getBlock().getName();

                    valuesMatch = values.contains(valueName) || checkingState.getValue(checkingProperty) == originalState.getValue(originalProperty);
                }
            }
        }

        return propertiesMatch && valuesMatch;
    }
}
