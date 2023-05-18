package nl.curryducker.seamless.fabric.mixin;

import net.mehvahdjukaar.sleep_tight.common.HammockPart;
import net.mehvahdjukaar.sleep_tight.common.blocks.HammockBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HammockBlock.class)
public abstract class HammockBlockMixin extends HorizontalDirectionalBlock {

    @Shadow @Final public static EnumProperty<HammockPart> PART;

    public HammockBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        String part = switch (state.getValue(PART)) {
            case FOOT -> "foot";
            case HEAD -> "head";
            case MIDDLE -> "middle";
            case HALF_FOOT -> "half-foot";
            case HALF_HEAD -> "half-head";
        };

        cir.setReturnValue(SeamlessShapes.hammock(part, state.getValue(FACING)));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        VoxelShape SHAPE_FULL = Block.box(0.0, 3.0, 0.0, 16.0, 6.0, 16.0);
        VoxelShape SHAPE_NORTH = Block.box(0.0, 3.0, 8.0, 16.0, 6.0, 16.0);
        VoxelShape SHAPE_SOUTH = Block.box(0.0, 3.0, 0.0, 16.0, 6.0, 8.0);
        VoxelShape SHAPE_WEST = Block.box(8.0, 3.0, 0.0, 16.0, 6.0, 16.0);
        VoxelShape SHAPE_EAST = Block.box(0.0, 3.0, 0.0, 8.0, 6.0, 16.0);

        HammockPart part = blockState.getValue(PART);
        if (!part.isOnFence() && part != HammockPart.MIDDLE) {
            VoxelShape var10000;
            switch (part.getConnectionDirection(blockState.getValue(FACING))) {
                case WEST:
                    var10000 = SHAPE_WEST;
                    break;
                case EAST:
                    var10000 = SHAPE_EAST;
                    break;
                case SOUTH:
                    var10000 = SHAPE_SOUTH;
                    break;
                default:
                    var10000 = SHAPE_NORTH;
            }

            return var10000;
        } else {
            return SHAPE_FULL;
        }
    }
}
