package nl.curryducker.seamless;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class Something {
    public VoxelShape voxelShape;
    public List<BlockPos> blockPosList;

    public Something(VoxelShape voxelShape, List<BlockPos> blockPosList) {
        this.voxelShape = voxelShape;
        this.blockPosList = blockPosList;
    }
}
