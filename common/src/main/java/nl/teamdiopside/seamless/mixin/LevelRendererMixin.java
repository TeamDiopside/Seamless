package nl.teamdiopside.seamless.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.OutlineFinder;
import nl.teamdiopside.seamless.Seamless;
import org.jetbrains.annotations.NotNull;
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

    @Unique private VoxelShape seamless$lastShape = Shapes.block();
    @Unique private BlockPos seamless$lastPos = BlockPos.ZERO;
    @Unique private BlockState seamless$lastState = Blocks.AIR.defaultBlockState();

    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, int i, CallbackInfo ci) {

        if (Seamless.modIds.contains("diagonalfences") && blockState.is(BlockTags.FENCES)
                || Seamless.modIds.contains("diagonalwindows") && (blockState.is(Blocks.IRON_BARS) || blockState.is(getTag("forge:glass_panes")) || blockState.is(getTag("c:glass_panes")))
                || Seamless.modIds.contains("diagonalwalls") && blockState.is(BlockTags.WALLS)) {
            return;
        }

        VoxelShape shape;

        if (Seamless.fastEnabled) {
            if (Objects.equals(seamless$lastState, blockState) && Objects.equals(seamless$lastPos, blockPos)) {
                shape = seamless$lastShape;
            } else {
                OutlineFinder.Recursion recursion = OutlineFinder.findAndAddShapes(level, blockState, blockPos, new HashSet<>(), blockPos, entity);
                shape = recursion.voxelShape().optimize();

                seamless$lastShape = shape;
                seamless$lastPos = blockPos;
                seamless$lastState = blockState;
            }
        } else {
            OutlineFinder.Recursion recursion = OutlineFinder.findAndAddShapes(level, blockState, blockPos, new HashSet<>(), blockPos, entity);
            shape = recursion.voxelShape().optimize();
        }

        ShapeRenderer.renderShape(poseStack, vertexConsumer, shape, (double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f, i);
        ci.cancel();
    }

    private static @NotNull TagKey<Block> getTag(String string) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(string));
    }
}
