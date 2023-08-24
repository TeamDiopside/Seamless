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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.OutlineReloadListener;
import nl.teamdiopside.seamless.Recursion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.teamdiopside.seamless.OutlineReloadListener.RULES;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double i, double b, double c, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        Recursion recursion = recursiveShape(level, blockState, blockPos, new HashSet<>(), blockPos, entity);
        VoxelShape shape = recursion.voxelShape();

        LevelRenderer.renderShape(poseStack, vertexConsumer, shape, (double)blockPos.getX() - i, (double)blockPos.getY() - b, (double)blockPos.getZ() - c, 0.0f, 0.0f, 0.0f, 0.4f);
        ci.cancel();
    }

    private static Recursion recursiveShape(Level level, BlockState state, BlockPos pos, Set<BlockPos> connectedPositions, BlockPos originalPos, Entity entity) {
        connectedPositions.add(pos);
        BlockPos relativePos = pos.subtract(originalPos);
        VoxelShape shape = state.getShape(level, pos, CollisionContext.of(entity)).move(relativePos.getX(), relativePos.getY(), relativePos.getZ());

        for (OutlineReloadListener.OutlineRule outlineRule : RULES) {
            if (!outlineRule.blocks().contains(state.getBlock())) {
                continue;
            }
            for (Direction direction : outlineRule.directions()) {
                BlockPos checkingPos = pos.relative(direction);
                BlockState checkingState = level.getBlockState(checkingPos);

                if (!outlineRule.connectingBlocks().contains(checkingState.getBlock()) || connectedPositions.contains(checkingPos)) {
                    continue;
                }

                boolean blockstatesMatch = true;
                for (HashMap.Entry<String, List<String>> entry : outlineRule.connectingBlockstates().entrySet()) {
                    if (!entry.getValue().contains(checkingState.getValue(checkingState.getBlock().getStateDefinition().getProperty(entry.getKey())).toString())) {
                        blockstatesMatch = false;
                        break;
                    }
                }
                if (!blockstatesMatch) {
                    continue;
                }

                Recursion recursion = recursiveShape(level, checkingState, checkingPos, connectedPositions, originalPos, entity);
                shape = Shapes.or(shape, recursion.voxelShape());
                connectedPositions = recursion.connectedPositions();
            }
        }
        return new Recursion(shape, connectedPositions);
    }
}


