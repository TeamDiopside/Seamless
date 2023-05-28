package nl.curryducker.seamless.fabric.mixin;

import net.mehvahdjukaar.supplementaries.common.block.blocks.SpringLauncherBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mehvahdjukaar.supplementaries.common.block.blocks.SpringLauncherBlock.EXTENDED;

@Mixin(SpringLauncherBlock.class)
public abstract class SpringLauncherBlockMixin extends Block {

    @Shadow @Final public static DirectionProperty FACING;

    public SpringLauncherBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.getValue(EXTENDED)) {
            cir.setReturnValue(SeamlessShapes.piston(state.getValue(FACING), false, true));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

        // Vanilla VoxelShape
        VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        VoxelShape WEST_AABB = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
        VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
        VoxelShape UP_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
        VoxelShape DOWN_AABB = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);

        if (blockState.getValue(EXTENDED)) {
            switch (blockState.getValue(FACING)) {
                case DOWN: {
                    return DOWN_AABB;
                }
                default: {
                    return UP_AABB;
                }
                case NORTH: {
                    return NORTH_AABB;
                }
                case SOUTH: {
                    return SOUTH_AABB;
                }
                case WEST: {
                    return WEST_AABB;
                }
                case EAST:
            }
            return EAST_AABB;
        }
        return Shapes.block();
    }
}
