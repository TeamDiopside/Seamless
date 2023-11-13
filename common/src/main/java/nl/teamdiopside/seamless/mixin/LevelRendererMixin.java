package nl.teamdiopside.seamless.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.OutlineFinder;
import nl.teamdiopside.seamless.Seamless;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Objects;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {

    @Unique
    private VoxelShape lastShape = Shapes.block();
    @Unique
    private BlockPos lastPos = BlockPos.ZERO;
    @Unique
    private BlockState lastState = Blocks.AIR.defaultBlockState();

    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double i, double b, double c, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        VoxelShape shape;

        if (Seamless.fastEnabled) {
            if (Objects.equals(lastState, blockState) && Objects.equals(lastPos, blockPos)) {
                shape = lastShape;
            } else {
                OutlineFinder.Recursion recursion = OutlineFinder.findAndAddShapes(level, blockState, blockPos, new HashSet<>(), blockPos, entity);
                shape = recursion.voxelShape().optimize();

                lastShape = shape;
                lastPos = blockPos;
                lastState = blockState;
            }
        } else {
            OutlineFinder.Recursion recursion = OutlineFinder.findAndAddShapes(level, blockState, blockPos, new HashSet<>(), blockPos, entity);
            shape = recursion.voxelShape().optimize();
        }


        LevelRenderer.renderShape(poseStack, vertexConsumer, shape, (double)blockPos.getX() - i, (double)blockPos.getY() - b, (double)blockPos.getZ() - c, 0.0f, 0.0f, 0.0f, 0.4f);
        ci.cancel();
    }
}
