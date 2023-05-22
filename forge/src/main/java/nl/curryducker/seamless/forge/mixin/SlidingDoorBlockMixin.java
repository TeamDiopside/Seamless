package nl.curryducker.seamless.forge.mixin;

import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlidingDoorBlock.class)
public abstract class SlidingDoorBlockMixin extends DoorBlock {
    @Shadow @Final public static BooleanProperty VISIBLE;

    @Shadow public abstract boolean isFoldingDoor();

    public SlidingDoorBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (!blockState.getValue(OPEN) || (blockState.getValue(VISIBLE) || blockGetter instanceof ContraptionWorld)) {
            cir.setReturnValue(SeamlessShapes.door(blockState.getValue(FACING), blockState.getValue(HALF)));
        } else {
            cir.setReturnValue(SeamlessShapes.slidingDoor(blockState.getValue(FACING), blockState.getValue(HALF) == DoubleBlockHalf.LOWER, blockState.getValue(HINGE) == DoorHingeSide.RIGHT, isFoldingDoor()));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

        // Vanilla VoxelShape
        boolean folding = isFoldingDoor();
        VoxelShape se = folding ? Block.box(0.0, 0.0, -3.0, 9.0, 16.0, 3.0) : Block.box(0, 0, -13, 3, 16, 3);
        VoxelShape es = folding ? Block.box(-3.0, 0.0, 0.0, 3.0, 16.0, 9.0) : Block.box(-13, 0, 0, 3, 16, 3);
        VoxelShape nw = folding ? Block.box(7.0, 0.0, 13.0, 16.0, 16.0, 19.0) : Block.box(13, 0, 13, 16, 16, 29);
        VoxelShape wn = folding ? Block.box(13.0, 0.0, 7.0, 19.0, 16.0, 16.0) : Block.box(13, 0, 13, 29, 16, 16);
        VoxelShape sw = folding ? Block.box(7.0, 0.0, -3.0, 16.0, 16.0, 3.0) : Block.box(13, 0, -13, 16, 16, 3);
        VoxelShape ws = folding ? Block.box(13.0, 0.0, 0.0, 19.0, 16.0, 9.0) : Block.box(13, 0, 0, 29, 16, 3);
        VoxelShape ne = folding ? Block.box(0.0, 0.0, 13.0, 9.0, 16.0, 19.0) : Block.box(0, 0, 13, 3, 16, 29);
        VoxelShape en = folding ? Block.box(-3.0, 0.0, 7.0, 3.0, 16.0, 16.0) : Block.box(-13, 0, 13, 3, 16, 16);

        if (!(Boolean)pState.getValue(OPEN) && (pState.getValue(VISIBLE) || pLevel instanceof ContraptionWorld)) {
            return super.getCollisionShape(pState, pLevel, pPos, pContext);
        } else {
            Direction direction = pState.getValue(FACING);
            boolean hinge = pState.getValue(HINGE) == DoorHingeSide.RIGHT;

            return switch (direction) {
                case SOUTH -> hinge ? es : ws;
                case WEST -> hinge ? sw : nw;
                case NORTH -> hinge ? wn : en;
                default -> hinge ? ne : se;
            };
        }
    }
}
