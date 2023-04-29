package nl.curryducker.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.Voxelshapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.DoorBlock.*;

@Mixin(DoorBlock.class)
public class DoorBlockMixin extends Block {

    public DoorBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        DoubleBlockHalf half = blockState.getValue(HALF);
        Direction direction = blockState.getValue(FACING);
        boolean bl = !blockState.getValue(OPEN);
        boolean bl2 = blockState.getValue(HINGE) == DoorHingeSide.RIGHT;

        VoxelShape north = Voxelshapes.door(Direction.NORTH, half);
        VoxelShape east = Voxelshapes.door(Direction.EAST, half);
        VoxelShape south = Voxelshapes.door(Direction.SOUTH, half);
        VoxelShape west = Voxelshapes.door(Direction.WEST, half);

        switch (direction) {
            case NORTH -> cir.setReturnValue(bl ? north : (bl2 ? west : east));
            case EAST -> cir.setReturnValue(bl ? east : (bl2 ? north : south));
            case SOUTH -> cir.setReturnValue(bl ? south : (bl2 ? east : west));
            case WEST -> cir.setReturnValue(bl ? west : (bl2 ? south : north));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

        // Vanilla VoxelShape
        VoxelShape south = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        VoxelShape north = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        VoxelShape west = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape east = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

        Direction direction = blockState.getValue(FACING);
        boolean bl = !blockState.getValue(OPEN);
        boolean bl2 = blockState.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch (direction) {
            default: {
                return bl ? east : (bl2 ? north : south);
            }
            case SOUTH: {
                return bl ? south : (bl2 ? east : west);
            }
            case WEST: {
                return bl ? west : (bl2 ? south : north);
            }
            case NORTH:
        }
        return bl ? north : (bl2 ? west : east);
    }
}
