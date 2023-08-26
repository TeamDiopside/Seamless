package nl.teamdiopside.seamless.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.OutlineReloadListener;
import nl.teamdiopside.seamless.Recursion;
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

import static nl.teamdiopside.seamless.OutlineReloadListener.RULES;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {

    @Shadow private @Nullable ClientLevel level;

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

        for (OutlineReloadListener.OutlineRule outlineRule : RULES) {
            if (!outlineRule.blocks().contains(state.getBlock())) {
                continue;
            }

            boolean blockstatesMatch = true;
            for (HashMap.Entry<String, Set<String>> entry : outlineRule.blockstates().entrySet()) {
                Property<?> property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
                assert property != null : "Blockstate property " + entry.getKey() + " does not exist for " + state.getBlock().getName();
                boolean propertyEquals = entry.getKey().equals(property.getName());
                boolean valueEquals = entry.getValue().contains(state.getValue(property).toString());
                if (!(propertyEquals && valueEquals)) {
                    blockstatesMatch = false;
                    break;
                }
            }
            if (!blockstatesMatch) {
                continue;
            }

            for (Direction direction : outlineRule.directions()) {
                BlockPos checkingPos = pos.relative(direction);
                BlockState checkingState = level.getBlockState(checkingPos);

                if (!outlineRule.connectingBlocks().contains(checkingState.getBlock()) || connectedPositions.contains(checkingPos)) {
                    continue;
                }

                boolean connectingBlockstatesMatch = true;
                for (HashMap.Entry<String, Set<String>> entry : outlineRule.connectingBlockstates().entrySet()) {
                    Property<?> property = checkingState.getBlock().getStateDefinition().getProperty(entry.getKey());
                    assert property != null : "Blockstate property " + entry.getKey() + " does not exist for " + checkingState.getBlock().getName();
                    boolean propertyEquals = entry.getKey().equals(property.getName());
                    boolean valueEquals = entry.getValue().contains(checkingState.getValue(property).toString());
                    if (!(propertyEquals && valueEquals)) {
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
    private static boolean propertyDoesntMatch(BlockState blockState, String propertyName, Set<String> values) {
        Property<?> property = blockState.getBlock().getStateDefinition().getProperty(propertyName);
        assert property != null : "Blockstate property " + propertyName + " does not exist for " + blockState.getBlock().getName();
        boolean propertyEquals = propertyName.equals(property.getName());
        boolean valueEquals = values.contains(blockState.getValue(property).toString());
        return !(propertyEquals && valueEquals);
    }
}
