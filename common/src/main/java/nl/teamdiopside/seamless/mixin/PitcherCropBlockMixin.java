package nl.teamdiopside.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PitcherCropBlock.class)
public abstract class PitcherCropBlockMixin extends DoublePlantBlock implements BonemealableBlock {

    @Shadow @Final public static IntegerProperty AGE;

    @Shadow @Final private static VoxelShape COLLISION_SHAPE_BULB;

    @Shadow @Final private static VoxelShape COLLISION_SHAPE_CROP;

    protected PitcherCropBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(SeamlessShapes.pitcherCrop(blockState.getValue(HALF) == DoubleBlockHalf.LOWER, blockState.getValue(AGE)));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (blockState.getValue(AGE) == 0) {
            return COLLISION_SHAPE_BULB;
        }
        if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return COLLISION_SHAPE_CROP;
        }
        return super.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }
}
