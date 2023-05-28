package nl.curryducker.seamless;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class SeamlessShapes {
    public static VoxelShape extendShape(Direction direction, int distance, double x1, double y1, double z1, double x2, double y2, double z2) {
        int a = 16 * distance;
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

    public static VoxelShape extendShape(Direction direction, int distance, VoxelShape shape) {
        VoxelShape[] newshape = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((a, b, c, d, e, f) -> newshape[0] = Shapes.or(newshape[0], extendShape(direction, distance, a * 16, b * 16, c * 16, d * 16, e * 16, f * 16)));
        return newshape[0];
    }

    public static VoxelShape extendShape(Direction direction, VoxelShape voxelShape) {
        return extendShape(direction, 1, voxelShape);
    }
    
    public static VoxelShape moveShape(Direction direction, int distance, double x1, double y1, double z1, double x2, double y2, double z2) {
        distance *= 16;
        switch (direction) {
            case NORTH -> {
                z1-=distance;
                z2-=distance;
            }
            case EAST -> {
                x1+=distance;
                x2+=distance;
            }
            case SOUTH -> {
                z1+=distance;
                z2+=distance;
            }
            case WEST -> {
                x1-=distance;
                x2-=distance;
            }
            case UP -> {
                y1+=distance;
                y2+=distance;
            }
            case DOWN -> {
                y1-=distance;
                y2-=distance;
            }
        }
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape moveShape(Direction direction, int distance, VoxelShape shape) {
        VoxelShape[] newshape = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((a, b, c, d, e, f) -> newshape[0] = Shapes.or(newshape[0], moveShape(direction, distance, a * 16, b * 16, c * 16, d * 16, e * 16, f * 16)));
        return newshape[0];
    }

    public static VoxelShape moveShape(Direction direction,  VoxelShape voxelShape) {
        return moveShape(direction, 1, voxelShape);
    }

    public static VoxelShape doubleBlock(Direction direction, boolean bl, VoxelShape shape, VoxelShape movingShape) {
        return bl ? Shapes.or(shape, moveShape(direction, movingShape)) : Shapes.or(movingShape, moveShape(direction.getOpposite(), shape));
    }

    public static VoxelShape doubleHighSameShape(boolean bottom, VoxelShape shape) {
        return extendShape(bottom ? Direction.UP : Direction.DOWN, shape);
    }

    public static VoxelShape fullDoubleHigh(boolean bottom) {
        return doubleHighSameShape(bottom, Block.box(0, 0, 0, 16, 16, 16));
    }

    public static VoxelShape bed(Direction direction, boolean foot) {
        VoxelShape leg_nw = Block.box(0, 0, 0, 3, 3, 3);
        VoxelShape leg_sw = Block.box(0, 0, 13, 3, 3, 16);
        VoxelShape leg_ne = Block.box(13, 0, 0, 16, 3, 3);
        VoxelShape leg_se = Block.box(13, 0, 13, 16, 3, 16);
        VoxelShape body = Block.box(0, 3, 0, 16, 9, 16);

        VoxelShape north = Shapes.or(leg_se, leg_sw, body);
        VoxelShape east = Shapes.or(leg_nw, leg_sw, body);
        VoxelShape south = Shapes.or(leg_ne, leg_nw, body);
        VoxelShape west = Shapes.or(leg_ne, leg_se, body);

        switch (direction) {
            case NORTH -> {
                return doubleBlock(direction, foot, north, south);
            }
            case EAST -> {
                return doubleBlock(direction, foot, east, west);
            }
            case SOUTH -> {
                return doubleBlock(direction, foot, south, north);
            }
        }
        return doubleBlock(direction, foot, west, east);
    }

    public static VoxelShape door(Direction direction, boolean bottom) {
        VoxelShape north = Block.box(0, 0, 13, 16, 16, 16);
        VoxelShape east = Block.box(0, 0, 0, 3, 16, 16);
        VoxelShape south = Block.box(0, 0, 0, 16, 16, 3);
        VoxelShape west = Block.box(13, 0, 0, 16, 16, 16);

        switch (direction) {
            case NORTH -> {
                return doubleHighSameShape(bottom, north);
            }
            case EAST -> {
                return doubleHighSameShape(bottom, east);
            }
            case SOUTH -> {
                return doubleHighSameShape(bottom, south);
            }
        }
        return doubleHighSameShape(bottom, west);
    }

    public static VoxelShape smallDripleaf(boolean bottom) {
        return doubleHighSameShape(bottom, Block.box(2, 0, 2, 14, 13, 14));
    }

    public static VoxelShape tallSeaGrass(boolean bottom) {
        return doubleHighSameShape(bottom, Block.box(2, 0, 2, 14, 16, 14));
    }

    public static VoxelShape piston(Direction direction, boolean isHead, boolean wideArm) {
        VoxelShape north_head = Block.box(0, 0, 0, 16, 16, 4);
        VoxelShape east_head = Block.box(12, 0, 0, 16, 16, 16);
        VoxelShape south_head = Block.box(0, 0, 12, 16, 16, 16);
        VoxelShape west_head = Block.box(0, 0, 0, 4, 16, 16);
        VoxelShape up_head = Block.box(0, 12, 0, 16, 16, 16);
        VoxelShape down_head = Block.box(0, 0, 0, 16, 4, 16);

        double d = wideArm ? 1 : 6;
        double e = wideArm ? 15 : 10;
        VoxelShape north_arm = Block.box(d, d, 4, e, e, 20);
        VoxelShape east_arm = Block.box(-4, d, d, 12, e, e);
        VoxelShape south_arm = Block.box(d, d, -4, e, e, 12);
        VoxelShape west_arm = Block.box(4, d, d, 20, e, e);
        VoxelShape up_arm = Block.box(d, -4, d, e, 12, e);
        VoxelShape down_arm = Block.box(d, 4, d, e, 20, e);

        VoxelShape north_base = Block.box(0, 0, 4, 16, 16, 16);
        VoxelShape east_base = Block.box(0, 0, 0, 12, 16, 16);
        VoxelShape south_base = Block.box(0, 0, 0, 16, 16, 12);
        VoxelShape west_base = Block.box(4, 0, 0, 16, 16, 16);
        VoxelShape up_base = Block.box(0, 0, 0, 16, 12, 16);
        VoxelShape down_base = Block.box(0, 4, 0, 16, 16, 16);

        VoxelShape base = switch (direction) {
            case NORTH -> north_base;
            case EAST -> east_base;
            case SOUTH -> south_base;
            case WEST -> west_base;
            case UP -> up_base;
            case DOWN -> down_base;
        };

        VoxelShape head = switch (direction) {
            case NORTH -> Shapes.or(north_head, north_arm);
            case EAST -> Shapes.or(east_head, east_arm);
            case SOUTH -> Shapes.or(south_head, south_arm);
            case WEST -> Shapes.or(west_head, west_arm);
            case UP -> Shapes.or(up_head, up_arm);
            case DOWN -> Shapes.or(down_head, down_arm);
        };

        return doubleBlock(direction.getOpposite(), isHead, head, base);
    }

    public static VoxelShape chest(Direction direction) {
        return extendShape(direction, Block.box(1, 0, 1, 15, 14, 15));
    }

    public static VoxelShape mat(Direction direction, boolean bl) {
        return extendShape(bl ? direction : direction.getOpposite(), Block.box(0, 0, 0, 16, 2, 16));
    }

    public static VoxelShape flax(boolean isBottom, int age) {
        VoxelShape fullBottom = Block.box(1, 0, 1, 15, 16, 15);
        VoxelShape[] bottom = new VoxelShape[]{Block.box(4, 0, 4, 12, 6, 12), Block.box(3, 0, 3, 13, 10, 13), Block.box(3, 0, 3, 13, 13, 13), Block.box(3, 0, 3, 13, 16, 13), Block.box(2, 0, 2, 14, 16, 14), fullBottom, fullBottom, fullBottom};
        VoxelShape[] top = new VoxelShape[]{fullBottom, fullBottom, fullBottom, fullBottom, Block.box(2, 0, 2, 14, 3, 14), Block.box(1, 0, 1, 15, 7, 15), Block.box(1, 0, 1, 15, 14, 15), Block.box(1, 0, 1, 15, 16, 15)};
        return doubleBlock(Direction.UP, isBottom, bottom[age], top[age]);
    }

    public static VoxelShape rice(int bottomAge, int topAge, boolean isBottom) {
        VoxelShape[] bottom = new VoxelShape[]{Block.box(3, 0, 3, 13, 8, 13), Block.box(3, 0, 3, 13, 10, 13), Block.box(2, 0, 2, 14, 12, 14), Block.box(1, 0, 1, 15, 16, 15)};
        VoxelShape[] top = new VoxelShape[]{Block.box(3, 0, 3, 13, 8, 13), Block.box(3, 0, 3, 13, 10, 13), Block.box(2, 0, 2, 14, 12, 14), Block.box(1, 0, 1, 15, 16, 15)};

        if (topAge == -1) {
            return bottom[bottomAge];
        }
        return doubleBlock(Direction.UP, isBottom, bottom[3], top[topAge]);
    }

    public static VoxelShape slidingDoor(Direction direction, boolean isBottom, boolean hingeRight, boolean folding) {
        VoxelShape se = folding ? Block.box(0.0, 0.0, -3.0, 9.0, 16.0, 3.0) : Block.box(0, 0, -13, 3, 16, 3);
        VoxelShape es = folding ? Block.box(-3.0, 0.0, 0.0, 3.0, 16.0, 9.0) : Block.box(-13, 0, 0, 3, 16, 3);
        VoxelShape nw = folding ? Block.box(7.0, 0.0, 13.0, 16.0, 16.0, 19.0) : Block.box(13, 0, 13, 16, 16, 29);
        VoxelShape wn = folding ? Block.box(13.0, 0.0, 7.0, 19.0, 16.0, 16.0) : Block.box(13, 0, 13, 29, 16, 16);
        VoxelShape sw = folding ? Block.box(7.0, 0.0, -3.0, 16.0, 16.0, 3.0) : Block.box(13, 0, -13, 16, 16, 3);
        VoxelShape ws = folding ? Block.box(13.0, 0.0, 0.0, 19.0, 16.0, 9.0) : Block.box(13, 0, 0, 29, 16, 3);
        VoxelShape ne = folding ? Block.box(0.0, 0.0, 13.0, 9.0, 16.0, 19.0) : Block.box(0, 0, 13, 3, 16, 29);
        VoxelShape en = folding ? Block.box(-3.0, 0.0, 7.0, 3.0, 16.0, 16.0) : Block.box(-13, 0, 13, 3, 16, 16);

        return doubleHighSameShape(isBottom, switch (direction) {
            case DOWN, UP, NORTH -> hingeRight ? wn : en;
            case EAST -> hingeRight ? ne : se;
            case SOUTH -> hingeRight ? es : ws;
            case WEST -> hingeRight ? sw : nw;
        });
    }

    public static VoxelShape brimstoneClusterBlock(boolean isBottom) {
        VoxelShape shape = Block.box(3, 0, 3, 13, 16, 13);
        VoxelShape shapeTop = Block.box(6, 0, 6, 10, 8, 10);
        return doubleBlock(Direction.UP, isBottom, shape, shapeTop);
    }

    public static VoxelShape corn(int plantLevel, int baseAge, int midAge, int topAge) {
        VoxelShape[] baseShapes = new VoxelShape[]{Block.box(6, 0, 6, 10, 4, 10), Block.box(5, 0, 5, 11, 8, 11), Block.box(3, 0, 3, 13, 12, 13), Block.box(1, 0, 1, 15, 16, 15)};
        VoxelShape[] middleShapes = new VoxelShape[]{Block.box(1, 0, 1, 15, 5, 15), Block.box(1, 0, 1, 15, 11, 15), Block.box(1, 0, 1, 15, 16, 15)};
        VoxelShape[] topShapes = new VoxelShape[]{Block.box(1, 0, 1, 15, 5, 15), Block.box(1, 0, 1, 15, 14, 15)};

        VoxelShape base = baseShapes[baseAge];
        VoxelShape middle = middleShapes[midAge];
        VoxelShape top = topShapes[topAge];

        VoxelShape shape = Shapes.empty();

        switch (plantLevel) {
            case 0 -> {
                shape = Shapes.or(shape, base);
                if (baseAge == 3) {
                    shape = Shapes.or(shape, moveShape(Direction.UP, middle));
                    if (midAge == 2) {
                        shape = Shapes.or(shape, moveShape(Direction.UP, 2, top));
                    }
                }
            }
            case 1 -> {
                shape = Shapes.or(shape, moveShape(Direction.DOWN, base), middle);
                if (midAge == 2) {
                    shape = Shapes.or(shape, moveShape(Direction.UP, top));
                }
            }
            case 2 -> shape = Shapes.or(shape, moveShape(Direction.DOWN, 2, base), moveShape(Direction.DOWN, middle), top);
        }
        return shape;
    }

    public static VoxelShape hammock(String part, Direction direction) {
        VoxelShape full = Block.box(0, 3, 0, 16, 6, 16);

        if (Objects.equals(part, "head") || Objects.equals(part, "half-head")) {
            direction = direction.getOpposite();
        }

        if (Objects.equals(part, "foot") || Objects.equals(part, "head")) {
            return Shapes.or(fenceHammock(direction.getOpposite()), moveShape(direction, fenceHammock(direction)));
        }

        if (Objects.equals(part, "middle")) {
            return Shapes.or(full, moveShape(direction.getOpposite(), blockHammock(direction.getOpposite())), moveShape(direction, blockHammock(direction)));
        }

        return Shapes.or(blockHammock(direction.getOpposite()), moveShape(direction, full), moveShape(direction, 2, blockHammock(direction)));
    }

    public static VoxelShape blockHammock(Direction direction) {
        VoxelShape north = Block.box(0, 3, 6, 16, 6, 16);
        VoxelShape south = Block.box(0, 3, 0, 16, 6, 10);
        VoxelShape west = Block.box(6, 3, 0, 16, 6, 16);
        VoxelShape east = Block.box(0, 3, 0, 10, 6, 16);

        return switch (direction) {
            case DOWN, UP, NORTH -> north;
            case WEST -> west;
            case EAST -> east;
            case SOUTH -> south;
        };
    }

    public static VoxelShape fenceHammock(Direction direction) {
        VoxelShape north = Block.box(0, 3, -2, 16, 6, 16);
        VoxelShape east = Block.box(0, 3, 0, 18, 6, 16);
        VoxelShape south = Block.box(0, 3, 0, 16, 6, 18);
        VoxelShape west = Block.box(-2, 3, 0, 16, 6, 16);

        return switch (direction) {
            case DOWN, UP, NORTH -> north;
            case WEST -> west;
            case EAST -> east;
            case SOUTH -> south;
        };
    }

    public static VoxelShape tallDeadBush(boolean bottom) {
        VoxelShape lower = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
        VoxelShape upper = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
        return doubleBlock(Direction.UP, bottom, lower, upper);
    }

    public static VoxelShape cattail(boolean bottom) {
        VoxelShape lower = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
        VoxelShape upper = Block.box(3.0, 0.0, 3.0, 13.0, 14.0, 13.0);
        return doubleBlock(Direction.UP, bottom, lower, upper);
    }

    public static VoxelShape largeLilyPad(String pos) {
        VoxelShape shape = Block.box(0.0, 0.0, 0.0, 16.0, 3.2001, 16.0);
        if (pos.contains("north")) shape = extendShape(Direction.SOUTH, shape);
        if (pos.contains("east")) shape = extendShape(Direction.WEST, shape);
        if (pos.contains("south")) shape = extendShape(Direction.NORTH, shape);
        if (pos.contains("west")) shape = extendShape(Direction.EAST, shape);
        return shape;
    }

    public static VoxelShape giantLilyPad(String pos) {
        VoxelShape shape = Block.box(0.0, 0.0, 0.0, 16.0, 3.2001, 16.0);
        if (pos.contains("north")) shape = extendShape(Direction.SOUTH, 2, shape);
        if (pos.contains("east")) shape = extendShape(Direction.WEST, 2, shape);
        if (pos.contains("south")) shape = extendShape(Direction.NORTH, 2, shape);
        if (pos.contains("west")) shape = extendShape(Direction.EAST, 2, shape);
        if (pos.equals("north") || pos.equals("south")) shape = extendShape(Direction.EAST, extendShape(Direction.WEST, shape));
        if (pos.equals("east") || pos.equals("west")) shape = extendShape(Direction.NORTH, extendShape(Direction.SOUTH, shape));
        if (pos.equals("center")) shape = extendShape(Direction.NORTH, extendShape(Direction.EAST, extendShape(Direction.SOUTH, extendShape(Direction.WEST, shape))));
        return shape;
    }
}
