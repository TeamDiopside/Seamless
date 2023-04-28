package nl.curryducker.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.Voxelshapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public class DoorBlockMixin extends Block {
    @Shadow @Final public static EnumProperty<DoubleBlockHalf> HALF;
    @Shadow @Final public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    @Shadow @Final public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    @Shadow @Final public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

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
}
