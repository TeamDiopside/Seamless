package nl.curryducker.seamless.fabric.mixin;

import net.mehvahdjukaar.hauntedharvest.blocks.AbstractCornBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornBaseBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornMiddleBlock;
import net.mehvahdjukaar.hauntedharvest.blocks.CornTopBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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

@Mixin(CornBaseBlock.class)
public abstract class CornBaseBlockMixin extends AbstractCornBlock {

    @Shadow @Final public static IntegerProperty AGE;

    public CornBaseBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        int age = state.getValue(AGE);
        int midAge = age == 3 ? worldIn.getBlockState(pos.above()).getValue(CornMiddleBlock.AGE) : 0;
        int topAge = midAge == 2 ? worldIn.getBlockState(pos.above(2)).getValue(CornTopBlock.AGE) : 0;
        cir.setReturnValue(SeamlessShapes.corn(0, age, midAge, topAge));
    }
}
