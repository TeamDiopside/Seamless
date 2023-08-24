package nl.teamdiopside.seamless;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;

public record Recursion(VoxelShape voxelShape, Set<BlockPos> connectedPositions) {
}
