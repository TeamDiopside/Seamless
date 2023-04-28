package nl.curryducker.seamless;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Voxelshapes {
    public static VoxelShape full(boolean bottom) {
        return bottom ? Block.box(0, 0, 0, 16, 32, 16) : Block.box(0, -16, 0, 16, 16, 16);
    }

    public static VoxelShape bed(Direction direction, BedPart part) {
        int a = 16;

        VoxelShape leg_nw = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
        VoxelShape leg_sw = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
        VoxelShape leg_ne = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
        VoxelShape leg_se = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);

        VoxelShape leg_nnw = Block.box(0.0, 0.0, 0.0-a, 3.0, 3.0, 3.0-a);
        VoxelShape leg_nne = Block.box(13.0, 0.0, 0.0-a, 16.0, 3.0, 3.0-a);
        VoxelShape leg_ene = Block.box(13.0+a, 0.0, 0.0, 16.0+a, 3.0, 3.0);
        VoxelShape leg_ese = Block.box(13.0+a, 0.0, 13.0, 16.0+a, 3.0, 16.0);
        VoxelShape leg_sse = Block.box(13.0, 0.0, 13.0+a, 16.0, 3.0, 16.0+a);
        VoxelShape leg_ssw = Block.box(0.0, 0.0, 13.0+a, 3.0, 3.0, 16.0+a);
        VoxelShape leg_wnw = Block.box(0.0-a, 0.0, 0.0, 3.0-a, 3.0, 3.0);
        VoxelShape leg_wsw = Block.box(0.0-a, 0.0, 13.0, 3.0-a, 3.0, 16.0);

        VoxelShape body_n = Block.box(0.0, 3.0, 0.0-a, 16.0, 9.0, 16.0);
        VoxelShape body_e = Block.box(0.0, 3.0, 0.0, 16.0+a, 9.0, 16.0);
        VoxelShape body_s = Block.box(0.0, 3.0, 0.0, 16.0, 9.0, 16.0+a);
        VoxelShape body_w = Block.box(0.0-a, 3.0, 0.0, 16.0, 9.0, 16.0);

        boolean isFoot = part == BedPart.FOOT;

        switch (direction) {
            case NORTH -> {
                return isFoot ? Shapes.or(leg_sw, leg_se, leg_nne, leg_nnw, body_n) :
                        Shapes.or(leg_ne, leg_nw, leg_sse, leg_ssw, body_s);
            }
            case EAST -> {
                return isFoot ? Shapes.or(leg_sw, leg_nw, leg_ene, leg_ese, body_e) :
                        Shapes.or(leg_ne, leg_se, leg_wnw, leg_wsw, body_w);
            }
            case SOUTH -> {
                return isFoot ? Shapes.or(leg_nw, leg_ne, leg_sse, leg_ssw, body_s) :
                        Shapes.or(leg_se, leg_sw, leg_nne, leg_nnw, body_n);
            }
        }
        return isFoot ? Shapes.or(leg_ne, leg_se, leg_wnw, leg_wsw, body_w) :
                Shapes.or(leg_sw, leg_nw, leg_ene, leg_ese, body_e);
    }

    public static VoxelShape door(Direction direction, DoubleBlockHalf half) {
        VoxelShape north_bottom = Block.box(0.0, 0.0, 13.0, 16.0, 32.0, 16.0);
        VoxelShape east_bottom = Block.box(0.0, 0.0, 0.0, 3.0, 32.0, 16.0);
        VoxelShape south_bottom = Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 3.0);
        VoxelShape west_bottom = Block.box(13.0, 0.0, 0.0, 16.0, 32.0, 16.0);
        VoxelShape north_top = Block.box(0.0, -16.0, 13.0, 16.0, 16.0, 16.0);
        VoxelShape east_top = Block.box(0.0, -16.0, 0.0, 3.0, 16.0, 16.0);
        VoxelShape south_top = Block.box(0.0, -16.0, 0.0, 16.0, 16.0, 3.0);
        VoxelShape west_top = Block.box(13.0, -16.0, 0.0, 16.0, 16.0, 16.0);

        boolean isBottom = half == DoubleBlockHalf.LOWER;
        switch (direction) {
            case NORTH -> {
                return isBottom ? north_bottom : north_top;
            }
            case EAST -> {
                return isBottom ? east_bottom : east_top;
            }
            case SOUTH -> {
                return isBottom ? south_bottom : south_top;
            }
        }
        return isBottom ? west_bottom : west_top;
    }

    public static VoxelShape smallDripleaf(boolean bottom) {
        return bottom ? Block.box(2, 0, 2, 14, 29, 14) : Block.box(2, -16, 2, 14, 13, 14);
    }
}
