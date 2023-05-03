package nl.curryducker.seamless;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeamlessShapes {
    public static VoxelShape extendShape(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2) {
        int a = 16;
        switch (direction) {
            case NORTH -> z1-=a;
            case EAST -> x2+=a;
            case SOUTH -> z2+=a;
            case WEST -> x1-=a;
            case UP -> y2+=a;
            case DOWN -> y1-=a;
        }
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape extendShape(Direction direction, VoxelShape voxelShape) {
        double x1 = voxelShape.min(Direction.Axis.X) * 16;
        double y1 = voxelShape.min(Direction.Axis.Y) * 16;
        double z1 = voxelShape.min(Direction.Axis.Z) * 16;
        double x2 = voxelShape.max(Direction.Axis.X) * 16;
        double y2 = voxelShape.max(Direction.Axis.Y) * 16;
        double z2 = voxelShape.max(Direction.Axis.Z) * 16;
        return extendShape(direction, x1, y1, z1, x2, y2, z2);
    }
    
    public static VoxelShape moveShape(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2) {
        int a = 16;
        switch (direction) {
            case NORTH -> {
                z1-=a;
                z2-=a;
            }
            case EAST -> {
                x1+=a;
                x2+=a;
            }
            case SOUTH -> {
                z1+=a;
                z2+=a;
            }
            case WEST -> {
                x1-=a;
                x2-=a;
            }
            case UP -> {
                y1+=a;
                y2+=a;
            }
            case DOWN -> {
                y1-=a;
                y2-=a;
            }
        }
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape moveShape(Direction direction, VoxelShape voxelShape) {
        double x1 = voxelShape.min(Direction.Axis.X) * 16;
        double y1 = voxelShape.min(Direction.Axis.Y) * 16;
        double z1 = voxelShape.min(Direction.Axis.Z) * 16;
        double x2 = voxelShape.max(Direction.Axis.X) * 16;
        double y2 = voxelShape.max(Direction.Axis.Y) * 16;
        double z2 = voxelShape.max(Direction.Axis.Z) * 16;
        return moveShape(direction, x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape full(boolean bottom) {
        return extendShape(bottom ? Direction.UP : Direction.DOWN, 0, 0, 0, 16, 16, 16);
    }

    public static VoxelShape bed(Direction direction, BedPart part) {
        int a = 16;

        VoxelShape leg_nw = Block.box(0, 0, 0, 3, 3, 3);
        VoxelShape leg_sw = Block.box(0, 0, 13, 3, 3, 16);
        VoxelShape leg_ne = Block.box(13, 0, 0, 16, 3, 3);
        VoxelShape leg_se = Block.box(13, 0, 13, 16, 3, 16);

        VoxelShape leg_nnw = moveShape(Direction.NORTH, leg_nw);
        VoxelShape leg_nne = moveShape(Direction.NORTH, leg_ne);
        VoxelShape leg_ene = moveShape(Direction.EAST, leg_ne);
        VoxelShape leg_ese = moveShape(Direction.EAST, leg_se);
        VoxelShape leg_sse = moveShape(Direction.SOUTH, leg_se);
        VoxelShape leg_ssw = moveShape(Direction.SOUTH, leg_sw);
        VoxelShape leg_wsw = moveShape(Direction.WEST, leg_sw);
        VoxelShape leg_wnw = moveShape(Direction.WEST, leg_nw);

        VoxelShape body = Block.box(0, 3, 0, 16, 9, 16);

        boolean isFoot = part == BedPart.FOOT;

        VoxelShape base = extendShape(isFoot ? direction : direction.getOpposite(), body);

        switch (direction) {
            case NORTH -> {
                return isFoot ? Shapes.or(leg_sw, leg_se, leg_nne, leg_nnw, base) :
                        Shapes.or(leg_ne, leg_nw, leg_sse, leg_ssw, base);
            }
            case EAST -> {
                return isFoot ? Shapes.or(leg_sw, leg_nw, leg_ene, leg_ese, base) :
                        Shapes.or(leg_ne, leg_se, leg_wnw, leg_wsw, base);
            }
            case SOUTH -> {
                return isFoot ? Shapes.or(leg_nw, leg_ne, leg_sse, leg_ssw, base) :
                        Shapes.or(leg_se, leg_sw, leg_nne, leg_nnw, base);
            }
        }
        return isFoot ? Shapes.or(leg_ne, leg_se, leg_wnw, leg_wsw, base) :
                Shapes.or(leg_sw, leg_nw, leg_ene, leg_ese, base);
    }

    public static VoxelShape door(Direction direction, DoubleBlockHalf half) {
        VoxelShape north = Block.box(0, 0, 13, 16, 16, 16);
        VoxelShape east = Block.box(0, 0, 0, 3, 16, 16);
        VoxelShape south = Block.box(0, 0, 0, 16, 16, 3);
        VoxelShape west = Block.box(13, 0, 0, 16, 16, 16);

        Direction vertical = half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN;
        switch (direction) {
            case NORTH -> {
                return extendShape(vertical, north);
            }
            case EAST -> {
                return extendShape(vertical, east);
            }
            case SOUTH -> {
                return extendShape(vertical, south);
            }
        }
        return extendShape(vertical, west);
    }

    public static VoxelShape smallDripleaf(boolean bottom) {
        return extendShape(bottom ? Direction.UP : Direction.DOWN, 2, 0, 2, 14, 13, 14);
    }

    public static VoxelShape tallSeaGrass(boolean bottom) {
        return extendShape(bottom ? Direction.UP : Direction.DOWN,2, 0, 2, 14, 16, 14);
    }

    public static VoxelShape piston(Direction direction, boolean isHead) {
        VoxelShape east_head = Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape west_head = Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
        VoxelShape south_head = Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
        VoxelShape north_head = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
        VoxelShape up_head = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape down_head = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);

        VoxelShape up_arm = Block.box(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
        VoxelShape down_arm = Block.box(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
        VoxelShape south_arm = Block.box(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
        VoxelShape north_arm = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
        VoxelShape east_arm = Block.box(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        VoxelShape west_arm = Block.box(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);

        VoxelShape east_base = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        VoxelShape west_base = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        VoxelShape south_base = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
        VoxelShape north_base = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
        VoxelShape up_base = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
        VoxelShape down_base = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);

        VoxelShape base = switch (direction) {
            case NORTH -> north_base;
            case EAST -> east_base;
            case SOUTH -> south_base;
            case WEST -> west_base;
            case UP -> up_base;
            case DOWN -> down_base;
        };

        VoxelShape head = switch (direction) {
            case NORTH -> north_head;
            case EAST -> east_head;
            case SOUTH -> south_head;
            case WEST -> west_head;
            case UP -> up_head;
            case DOWN -> down_head;
        };

        VoxelShape arm = switch (direction) {
            case NORTH -> north_arm;
            case EAST -> east_arm;
            case SOUTH -> south_arm;
            case WEST -> west_arm;
            case UP -> up_arm;
            case DOWN -> down_arm;
        };

        if (isHead) {
            base = moveShape(direction.getOpposite(), base);
        } else {
            head = moveShape(direction, head);
            arm = moveShape(direction, arm);
        }

        return Shapes.or(base, head, arm);
    }

    public static VoxelShape bedroll(Direction direction, BedPart bedPart) {
        return extendShape(bedPart == BedPart.FOOT ? direction : direction.getOpposite(), 0, 0, 0, 16, 2, 16);
    }

    public static VoxelShape flax(boolean isBottom, int age) {
        VoxelShape FULL_BOTTOM = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
        VoxelShape[] SHAPES_BOTTOM = new VoxelShape[]{Block.box(4.0, 0.0, 4.0, 12.0, 6.0, 12.0), Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0), Block.box(3.0, 0.0, 3.0, 13.0, 13.0, 13.0), Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0), Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), FULL_BOTTOM, FULL_BOTTOM, FULL_BOTTOM};
        VoxelShape[] SHAPES_TOP = new VoxelShape[]{FULL_BOTTOM, FULL_BOTTOM, FULL_BOTTOM, FULL_BOTTOM, Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0), Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0), Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)};

        if (isBottom) {
            return Shapes.or(SHAPES_BOTTOM[age], moveShape(Direction.UP, SHAPES_TOP[age]));
        } else {
            return Shapes.or(SHAPES_TOP[age], moveShape(Direction.DOWN, SHAPES_BOTTOM[age]));
        }
    }
}
