package nl.curryducker.seamless.forge.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.RiceBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@Mixin(RiceBlock.class)
public class RiceCropBlockMixin extends BushBlock {

    @Shadow @Final public static IntegerProperty AGE;

    public RiceCropBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        int age = state.getValue(AGE);
        cir.setReturnValue(SeamlessShapes.rice(age, worldIn.getBlockState(pos.above()).getBlock() == ModBlocks.RICE_CROP_PANICLES.get() ? worldIn.getBlockState(pos.above()).getValue(AGE) : 0));
    }
}
