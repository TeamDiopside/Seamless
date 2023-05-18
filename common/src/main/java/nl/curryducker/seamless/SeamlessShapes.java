package nl.curryducker.seamless;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

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

    public static VoxelShape moveShape(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2) {
        return moveShape(direction, 1, x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape moveShape(Direction direction, int distance, VoxelShape voxelShape) {
        double x1 = voxelShape.min(Direction.Axis.X) * 16;
        double y1 = voxelShape.min(Direction.Axis.Y) * 16;
        double z1 = voxelShape.min(Direction.Axis.Z) * 16;
        double x2 = voxelShape.max(Direction.Axis.X) * 16;
        double y2 = voxelShape.max(Direction.Axis.Y) * 16;
        double z2 = voxelShape.max(Direction.Axis.Z) * 16;
        return moveShape(direction, distance, x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape moveShape(Direction direction, VoxelShape voxelShape) {
        return moveShape(direction, 1, voxelShape);
    }

    public static VoxelShape full(boolean bottom) {
        return extendShape(bottom ? Direction.UP : Direction.DOWN, 0, 0, 0, 16, 16, 16);
    }

    public static VoxelShape bed(Direction direction, BedPart part) {
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

    public static VoxelShape piston(Direction direction, boolean isHead, boolean wideArm) {
        VoxelShape east_head = Block.box(12, 0, 0, 16, 16, 16);
        VoxelShape west_head = Block.box(0, 0, 0, 4, 16, 16);
        VoxelShape south_head = Block.box(0, 0, 12, 16, 16, 16);
        VoxelShape north_head = Block.box(0, 0, 0, 16, 16, 4);
        VoxelShape up_head = Block.box(0, 12, 0, 16, 16, 16);
        VoxelShape down_head = Block.box(0, 0, 0, 16, 4, 16);

        double d = wideArm ? 1 : 6;
        double e = wideArm ? 15 : 10;
        VoxelShape up_arm = Block.box(d, -4, d, e, 12, e);
        VoxelShape down_arm = Block.box(d, 4, d, e, 20, e);
        VoxelShape south_arm = Block.box(d, d, -4, e, e, 12);
        VoxelShape north_arm = Block.box(d, d, 4, e, e, 20);
        VoxelShape east_arm = Block.box(-4, d, d, 12, e, e);
        VoxelShape west_arm = Block.box(4, d, d, 20, e, e);

        VoxelShape east_base = Block.box(0, 0, 0, 12, 16, 16);
        VoxelShape west_base = Block.box(4, 0, 0, 16, 16, 16);
        VoxelShape south_base = Block.box(0, 0, 0, 16, 16, 12);
        VoxelShape north_base = Block.box(0, 0, 4, 16, 16, 16);
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

    public static VoxelShape mat(Direction direction, boolean b) {
        return extendShape(b ? direction : direction.getOpposite(), 0, 0, 0, 16, 2, 16);
    }

    public static VoxelShape flax(boolean isBottom, int age) {
        VoxelShape fullBottom = Block.box(1, 0, 1, 15, 16, 15);
        VoxelShape[] bottom = new VoxelShape[]{Block.box(4, 0, 4, 12, 6, 12), Block.box(3, 0, 3, 13, 10, 13), Block.box(3, 0, 3, 13, 13, 13), Block.box(3, 0, 3, 13, 16, 13), Block.box(2, 0, 2, 14, 16, 14), fullBottom, fullBottom, fullBottom};
        VoxelShape[] top = new VoxelShape[]{fullBottom, fullBottom, fullBottom, fullBottom, Block.box(2, 0, 2, 14, 3, 14), Block.box(1, 0, 1, 15, 7, 15), Block.box(1, 0, 1, 15, 14, 15), Block.box(1, 0, 1, 15, 16, 15)};

        if (isBottom) {
            return Shapes.or(bottom[age], moveShape(Direction.UP, top[age]));
        } else {
            return Shapes.or(top[age], moveShape(Direction.DOWN, bottom[age]));
        }
    }

    public static VoxelShape rice(int age, int topAge) {
        VoxelShape[] bottom = new VoxelShape[]{Block.box(3, 0, 3, 13, 8, 13), Block.box(3, 0, 3, 13, 10, 13), Block.box(2, 0, 2, 14, 12, 14), Block.box(1, 0, 1, 15, 16, 15)};
        VoxelShape[] top = new VoxelShape[]{Block.box(3, 0, 3, 13, 8, 13), Block.box(3, 0, 3, 13, 10, 13), Block.box(2, 0, 2, 14, 12, 14), Block.box(1, 0, 1, 15, 16, 15)};

        if (age <= 2) {
            return bottom[age];
        }
        return Shapes.or(bottom[age], moveShape(Direction.UP, top[topAge]));
    }

    public static VoxelShape slidingDoor(Direction direction, boolean isBottom, boolean hingeRight) {
        VoxelShape se = Block.box(0, 0, -13, 3, 16, 3);
        VoxelShape es = Block.box(-13, 0, 0, 3, 16, 3);
        VoxelShape nw = Block.box(13, 0, 13, 16, 16, 29);
        VoxelShape wn = Block.box(13, 0, 13, 29, 16, 16);
        VoxelShape sw = Block.box(13, 0, -13, 16, 16, 3);
        VoxelShape ws = Block.box(13, 0, 0, 29, 16, 3);
        VoxelShape ne = Block.box(0, 0, 13, 3, 16, 29);
        VoxelShape en = Block.box(-13, 0, 13, 3, 16, 16);

        VoxelShape shape = switch (direction) {
            case DOWN, UP -> se; // just to make IntelliJ happy
            case NORTH -> hingeRight ? wn : en;
            case EAST -> hingeRight ? ne : se;
            case SOUTH -> hingeRight ? es : ws;
            case WEST -> hingeRight ? sw : nw;
        };

        return extendShape(isBottom ? Direction.UP : Direction.DOWN, shape);
    }

    public static VoxelShape brimstoneClusterBlock(boolean isBottom) {
        VoxelShape shape = Block.box(3, 0, 3, 13, 16, 13);
        VoxelShape shapeTop = Block.box(6, 0, 6, 10, 8, 10);
        if (isBottom) {
            return Shapes.or(shape, moveShape(Direction.UP, shapeTop));
        }
        return Shapes.or(shapeTop, moveShape(Direction.DOWN, shape));
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
            return Shapes.or(hammockDirectionFence(direction.getOpposite()), moveShape(direction, hammockDirectionFence(direction)));
        }

        if (Objects.equals(part, "middle")) {
            return Shapes.or(full, moveShape(direction.getOpposite(), hammockDirectionBlock(direction.getOpposite())), moveShape(direction, hammockDirectionBlock(direction)));
        }

        return Shapes.or(hammockDirectionBlock(direction.getOpposite()), moveShape(direction, full), moveShape(direction, 2, hammockDirectionBlock(direction)));
    }

    public static VoxelShape hammockDirectionBlock(Direction direction) {
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

    public static VoxelShape hammockDirectionFence(Direction direction) {
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

    public static VoxelShape chest(Direction direction) {
        return extendShape(direction, 1, 0, 1, 15, 14, 15);
    }
}
