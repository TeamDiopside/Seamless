package nl.teamdiopside.seamless.fabric.mixin;

import net.mehvahdjukaar.fastpaintings.PaintingBlock;
import net.mehvahdjukaar.fastpaintings.PaintingBlockEntity;
import net.mehvahdjukaar.moonlight.api.block.WaterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.teamdiopside.seamless.SeamlessShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PaintingBlock.class)
public abstract class PaintingBlockMixin extends WaterBlock implements EntityBlock {
    @Shadow @Final protected static DirectionProperty FACING;
    @Shadow @Final protected static IntegerProperty DOWN_OFFSET;
    @Shadow @Final protected static IntegerProperty RIGHT_OFFSET;

    protected PaintingBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        Direction facing = state.getValue(FACING);
        int x = state.getValue(RIGHT_OFFSET);
        int y = state.getValue(DOWN_OFFSET);
        BlockPos masterPos = pos.above(y).relative(facing.getClockWise(), x);

        PaintingVariant variant;
        if (level.getBlockEntity(masterPos) instanceof PaintingBlockEntity pe) {
            variant = pe.getVariant().value();
        } else return;

        int height = variant.getHeight();
        int width = variant.getWidth();
        cir.setReturnValue(SeamlessShapes.painting(facing, x, y, width / 16, height / 16));
    }
}
