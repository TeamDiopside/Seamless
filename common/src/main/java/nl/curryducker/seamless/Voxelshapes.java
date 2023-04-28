package nl.curryducker.seamless;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Voxelshapes {
    public static VoxelShape fullBottom() {
        return Block.box(0, 0, 0, 16, 32, 16);
    }
    public static VoxelShape fullTop() {
        return Block.box(0, -16, 0, 16, 16, 16);
    }
}
