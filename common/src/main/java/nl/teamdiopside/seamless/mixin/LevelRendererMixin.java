package nl.curryducker.seamless.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.Something;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static nl.curryducker.seamless.Outline.RULES;
import static nl.curryducker.seamless.Outline.rs;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double i, double b, double c, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        Block block = blockState.getBlock();
        var r = RULES.get(block);
        if (r != null) {
            for (var v : r) {
                if (v.selfTest().test(blockState, rs)) {
                    for (var e : v.targets().entrySet()) {
                        BlockState facing = entity.level().getBlockState(blockPos.relative(e.getKey()));
                        if (e.getValue().test(facing, rs)) {
                            VoxelShape otherShape = null;//todo: figure out recursiveness here
                            original = Shapes.or(original, otherShape);
                        }
                    }
                    break;
                }
            }
        }
        LevelRenderer.renderShape(poseStack, vertexConsumer, blockState.getShape(level, blockPos, CollisionContext.of(entity)), (double)blockPos.getX() - i, (double)blockPos.getY() - b, (double)blockPos.getZ() - c, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    private static Something recursion(Level level, BlockState state, BlockPos pos, List<BlockPos> previousPositions, VoxelShape shape, BlockPos originalPos, Entity entity) {
        var outlineRules = RULES.get(state.getBlock());
        previousPositions.add(pos);
        shape = Shapes.or(shape, state.getShape(level, pos, CollisionContext.of(entity)));
        if (outlineRules != null) {
            Direction[] directions = null; /* get all directions for this state specified in the json */
            for (Direction direction : directions) {
                BlockPos checkingPos = pos.relative(direction);
                BlockState checkingState = level.getBlockState(checkingPos);
                if (previousPositions.contains(checkingPos) /* && checkingState matches with whatever rule this state has */) {
                    BlockPos relativePos = pos.subtract(originalPos);
                    Something thing = recursion(level, checkingState, checkingPos, previousPositions, shape, originalPos, entity);
                    shape = Shapes.or(shape, thing.voxelShape.move(relativePos.getX(), relativePos.getY(), relativePos.getZ()));
                    previousPositions = thing.blockPosList;
                }
            }
        }
        return new Something(shape, previousPositions);
    }
}
