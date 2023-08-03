package nl.teamdiopside.seamless.forge.mixin;

import com.teamabnormals.atmospheric.common.block.YuccaFlowerDoubleBlock;
import com.teamabnormals.atmospheric.common.block.YuccaPlant;
import com.teamabnormals.blueprint.common.block.BlueprintTallFlowerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(YuccaFlowerDoubleBlock.class)
public abstract class YuccaFlowerDoubleBlockMixin extends BlueprintTallFlowerBlock implements YuccaPlant {

    public YuccaFlowerDoubleBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Vec3 vec3d = state.getOffset(worldIn, pos);
        cir.setReturnValue(SeamlessShapes.tallSeaGrass(state.getValue(HALF) == DoubleBlockHalf.LOWER).move(vec3d.x, vec3d.y, vec3d.z));
    }
}
