package nl.teamdiopside.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmallDripleafBlock.class)
public abstract class SmallDripleafBlockMixin extends DoublePlantBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    @Shadow @Final protected static VoxelShape SHAPE;

    protected SmallDripleafBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? Block.box(2, 0, 2, 14, 16, 14) : SHAPE);
    }
}
