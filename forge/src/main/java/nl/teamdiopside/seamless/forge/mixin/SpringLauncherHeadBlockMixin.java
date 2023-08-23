package nl.teamdiopside.seamless.forge.mixin;

import net.mehvahdjukaar.supplementaries.common.block.blocks.SpringLauncherHeadBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpringLauncherHeadBlock.class)
public abstract class SpringLauncherHeadBlockMixin extends DirectionalBlock {

    public SpringLauncherHeadBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(SeamlessShapes.piston(state.getValue(FACING), true, true));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

        // Vanilla VoxelShape
        VoxelShape EAST_AABB = Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
        VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
        VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
        VoxelShape UP_AABB = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

        VoxelShape UP_ARM_AABB = Block.box(1.0, -4.0, 1.0, 15.0, 12.0, 15.0);
        VoxelShape DOWN_ARM_AABB = Block.box(1.0, 4.0, 1.0, 15.0, 20.0, 15.0);
        VoxelShape SOUTH_ARM_AABB = Block.box(1.0, 1.0, -4.0, 15.0, 15.0, 12.0);
        VoxelShape NORTH_ARM_AABB = Block.box(1.0, 1.0, 4.0, 15.0, 15.0, 20.0);
        VoxelShape EAST_ARM_AABB = Block.box(-4.0, 1.0, 1.0, 12.0, 15.0, 15.0);
        VoxelShape WEST_ARM_AABB = Block.box(4.0, 1.0, 1.0, 20.0, 15.0, 15.0);

        switch (blockState.getValue(FACING)) {
            case NORTH -> {
                return Shapes.or(NORTH_AABB, NORTH_ARM_AABB);
            }
            case SOUTH -> {
                return Shapes.or(SOUTH_AABB, SOUTH_ARM_AABB);
            }
            case WEST -> {
                return Shapes.or(WEST_AABB, WEST_ARM_AABB);
            }
            case EAST -> {
                return Shapes.or(EAST_AABB, EAST_ARM_AABB);
            }
            case UP -> {
                return Shapes.or(UP_AABB, UP_ARM_AABB);
            }
        }
        return Shapes.or(DOWN_AABB, DOWN_ARM_AABB);
    }
}
