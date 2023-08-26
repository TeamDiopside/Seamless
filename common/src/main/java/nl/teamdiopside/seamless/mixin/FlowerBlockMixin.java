package nl.teamdiopside.seamless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerBlock.class)
public class FlowerBlockMixin {
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {

        VoxelShape shape = Shapes.empty();

//        for (double i = -10; i <= 20; i++) {
//            shape = Shapes.or(shape, Shapes.box(0, i / 10, 0, 0.1, (i + 1) / 10, 0.1));
//        }

//        for (double i = 0; i <= 10; i++) {
//            shape = Shapes.joinUnoptimized(shape, Shapes.box(i / 10, i / 10, i / 10, (i + 3) / 10, (i + 3) / 10, (i + 3) / 10), BooleanOp.OR);
//        }

        for (double i = 0; i <= 10; i++) {
            shape = Shapes.or(shape, Shapes.box(i / 10, i / 10, i / 10, (i + 3) / 10, (i + 3) / 10, (i + 3) / 10));
        }

        cir.setReturnValue(shape);
    }
}
