package nl.curryducker.seamless.fabric.mixin;

import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornMiddleBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornTopBlock;
import net.mehvahdjukaar.hauntedharvest.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CornMiddleBlock.class)
public abstract class CornMiddleBlockMixin extends AbstractCornBlock {

    public CornMiddleBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        int midAge = state.getValue(CornMiddleBlock.AGE);
        int topAge = worldIn.getBlockState(pos.above()).getBlock() == ModRegistry.CORN_TOP.get() ? worldIn.getBlockState(pos.above()).getValue(CornTopBlock.AGE) : 0;
        cir.setReturnValue(SeamlessShapes.corn(1, 3, midAge, topAge));
    }
}
