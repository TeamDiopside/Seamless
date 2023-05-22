package nl.curryducker.seamless.fabric.mixin;

import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionWorld;
import com.simibubi.create.content.curiosities.deco.SlidingDoorBlock;
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
public class SlidingDoorBlockMixin extends DoorBlock {
    @Shadow @Final public static BooleanProperty VISIBLE;

    public SlidingDoorBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (!blockState.getValue(OPEN) || (blockState.getValue(VISIBLE) || blockGetter instanceof ContraptionWorld)) {
            cir.setReturnValue(SeamlessShapes.door(blockState.getValue(FACING), blockState.getValue(HALF)));
        } else {
            cir.setReturnValue(SeamlessShapes.slidingDoor(blockState.getValue(FACING), blockState.getValue(HALF) == DoubleBlockHalf.LOWER, blockState.getValue(HINGE) == DoorHingeSide.RIGHT, false));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

        // Vanilla VoxelShape
        VoxelShape SE_AABB = Block.box(0.0, 0.0, -13.0, 3.0, 16.0, 3.0);
        VoxelShape ES_AABB = Block.box(-13.0, 0.0, 0.0, 3.0, 16.0, 3.0);
        VoxelShape NW_AABB = Block.box(13.0, 0.0, 13.0, 16.0, 16.0, 29.0);
        VoxelShape WN_AABB = Block.box(13.0, 0.0, 13.0, 29.0, 16.0, 16.0);
        VoxelShape SW_AABB = Block.box(13.0, 0.0, -13.0, 16.0, 16.0, 3.0);
        VoxelShape WS_AABB = Block.box(13.0, 0.0, 0.0, 29.0, 16.0, 3.0);
        VoxelShape NE_AABB = Block.box(0.0, 0.0, 13.0, 3.0, 16.0, 29.0);
        VoxelShape EN_AABB = Block.box(-13.0, 0.0, 13.0, 3.0, 16.0, 16.0);

        if (!(Boolean)pState.getValue(OPEN) && (pState.getValue(VISIBLE) || pLevel instanceof ContraptionWorld)) {
            return super.getCollisionShape(pState, pLevel, pPos, pContext);
        } else {
            Direction direction = pState.getValue(FACING);
            boolean hinge = pState.getValue(HINGE) == DoorHingeSide.RIGHT;

            return switch (direction) {
                case SOUTH -> hinge ? ES_AABB : WS_AABB;
                case WEST -> hinge ? SW_AABB : NW_AABB;
                case NORTH -> hinge ? WN_AABB : EN_AABB;
                default -> hinge ? NE_AABB : SE_AABB;
            };
        }
    }
}
