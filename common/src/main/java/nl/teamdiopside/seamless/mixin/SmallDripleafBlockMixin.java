package nl.teamdiopside.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmallDripleafBlock.class)
public abstract class SmallDripleafBlockMixin extends DoublePlantBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    protected SmallDripleafBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(SeamlessShapes.smallDripleaf(blockState.getValue(HALF) == DoubleBlockHalf.LOWER));
    }
}