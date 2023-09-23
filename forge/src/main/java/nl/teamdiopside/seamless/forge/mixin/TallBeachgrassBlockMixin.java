package nl.teamdiopside.seamless.forge.mixin;

import com.teamabnormals.upgrade_aquatic.common.block.TallBeachgrassBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TallBeachgrassBlock.class)
public abstract class TallBeachgrassBlockMixin extends Block implements BonemealableBlock {

    @Shadow @Final private static VoxelShape SHAPE;

    @Shadow @Final public static EnumProperty<DoubleBlockHalf> HALF;

    public TallBeachgrassBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Block.box(2, 0, 2, 14, 16, 14) : SHAPE);
    }
}
