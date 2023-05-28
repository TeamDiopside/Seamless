package nl.curryducker.seamless.forge.mixin;

import com.teamabnormals.atmospheric.common.block.WaterHyacinthBlock;
import com.teamabnormals.blueprint.common.block.BlueprintFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.curryducker.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(WaterHyacinthBlock.class)
public abstract class WaterHyacinthBlockMixin extends BlueprintFlowerBlock implements SimpleWaterloggedBlock {

    @Shadow @Final public static EnumProperty<DoubleBlockHalf> HALF;

    public WaterHyacinthBlockMixin(Supplier<MobEffect> stewEffect, int stewEffectDuration, Properties properties) {
        super(stewEffect, stewEffectDuration, properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Vec3 vec3d = state.getOffset(worldIn, pos);
        cir.setReturnValue(SeamlessShapes.tallSeaGrass(state.getValue(HALF) == DoubleBlockHalf.LOWER).move(vec3d.x, vec3d.y, vec3d.z));
    }
}
