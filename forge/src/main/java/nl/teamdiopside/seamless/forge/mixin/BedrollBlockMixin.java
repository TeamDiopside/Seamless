package nl.teamdiopside.seamless.forge.mixin;

import com.teamabnormals.upgrade_aquatic.common.block.BedrollBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedrollBlock.class)
public abstract class BedrollBlockMixin extends BedBlock implements SimpleWaterloggedBlock {
    @Shadow @Final private static VoxelShape SHAPE;

    public BedrollBlockMixin(DyeColor arg, Properties arg2) {
        super(arg, arg2);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(SeamlessShapes.mat(blockState.getValue(FACING), blockState.getValue(PART) == BedPart.FOOT));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
        return SHAPE;
    }
}