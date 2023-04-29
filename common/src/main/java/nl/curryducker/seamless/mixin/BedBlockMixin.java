package nl.curryducker.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.Voxelshapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin extends HorizontalDirectionalBlock {
    @Shadow @Final public static EnumProperty<BedPart> PART;

    protected BedBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(Voxelshapes.bed(blockState.getValue(FACING), blockState.getValue(PART)));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

        // Vanilla Voxelshape
        VoxelShape base = Block.box(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
        VoxelShape leg_north_west = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
        VoxelShape leg_south_west = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
        VoxelShape leg_north_east = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
        VoxelShape leg_south_east = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
        VoxelShape north_shape = Shapes.or(base, leg_north_west, leg_north_east);
        VoxelShape south_shape = Shapes.or(base, leg_south_west, leg_south_east);
        VoxelShape west_shape = Shapes.or(base, leg_north_west, leg_south_west);
        VoxelShape east_shape = Shapes.or(base, leg_north_east, leg_south_east);

        Direction direction = BedBlock.getConnectedDirection(blockState).getOpposite();
        switch (direction) {
            case NORTH: {
                return north_shape;
            }
            case SOUTH: {
                return south_shape;
            }
            case WEST: {
                return west_shape;
            }
        }
        return east_shape;
    }
}
