package nl.curryducker.seamless.forge.mixin;

import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornBaseBlock;
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

@Mixin(CornBaseBlock.class)
public abstract class CornBaseBlockMixin extends AbstractCornBlock {

    public CornBaseBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        int age = state.getValue(CornBaseBlock.AGE);
        int midAge = worldIn.getBlockState(pos.above()).getBlock() == ModRegistry.CORN_MIDDLE.get() ? worldIn.getBlockState(pos.above()).getValue(CornMiddleBlock.AGE) : 0;
        int topAge = worldIn.getBlockState(pos.above(2)).getBlock() == ModRegistry.CORN_TOP.get() ? worldIn.getBlockState(pos.above(2)).getValue(CornTopBlock.AGE) : 0;
        cir.setReturnValue(SeamlessShapes.corn(0, age, midAge, topAge));
    }
}
