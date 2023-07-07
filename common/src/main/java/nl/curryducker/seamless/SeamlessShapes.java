package nl.curryducker.seamless;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class SeamlessShapes {
    private static VoxelShape extendShape(Direction direction, int distance, double x1, double y1, double z1, double x2, double y2, double z2) {
        switch (direction) {
            case NORTH -> z1-=distance;
            case EAST -> x2+=distance;
            case SOUTH -> z2+=distance;
            case WEST -> x1-=distance;
            case UP -> y2+=distance;
            case DOWN -> y1-=distance;
        }
        return Shapes.box(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape extendShape(Direction direction, int distance, VoxelShape shape) {
        if (shape.isEmpty()) {
            return shape;
        }
        VoxelShape[] newshape = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((a, b, c, d, e, f) -> newshape[0] = Shapes.or(newshape[0], extendShape(direction, distance, a, b, c, d, e, f)));
        return newshape[0];
    }

    public static VoxelShape extendShape(Direction direction, VoxelShape voxelShape) {
        return extendShape(direction, 1, voxelShape);
    }
    
    private static VoxelShape moveShape(Direction direction, int distance, double x1, double y1, double z1, double x2, double y2, double z2) {
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
        return Shapes.box(x1, y1, z1, x2, y2, z2);
    }

    public static VoxelShape moveShape(Direction direction, int distance, VoxelShape shape) {
        if (shape.isEmpty()) {
            return shape;
        }
        VoxelShape[] newshape = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((a, b, c, d, e, f) -> newshape[0] = Shapes.or(newshape[0], moveShape(direction, distance, a, b, c, d, e, f)));
        return newshape[0];
    }

    public static VoxelShape moveShape(Direction direction, VoxelShape voxelShape) {
        return moveShape(direction, 1, voxelShape);
    }

    public static VoxelShape rotateShape(VoxelShape shape, Direction.Axis axis, int quarterTurns) {
        if (shape.isEmpty()) {
            return shape;
        }
        VoxelShape[] newshape = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            float angle = 0.5f * (float)Math.PI * quarterTurns;
            float align = 0.5f;
            Vec3 minVector = new Vec3(x1 - align, y1 - align, z1 - align);
            Vec3 maxVector = new Vec3(x2 - align, y2 - align, z2 - align);

            switch (axis) {
                case X -> {
                    minVector = minVector.xRot(angle);
                    maxVector = maxVector.xRot(angle);
                }
                case Y -> {
                    minVector = minVector.yRot(angle);
                    maxVector = maxVector.yRot(angle);
                }
                case Z -> {
                    minVector = minVector.zRot(angle);
                    maxVector = maxVector.zRot(angle);
                }
            }

            double a = Math.min(minVector.x, maxVector.x) + align;
            double b = Math.min(minVector.y, maxVector.y) + align;
            double c = Math.min(minVector.z, maxVector.z) + align;
            double d = Math.max(minVector.x, maxVector.x) + align;
            double e = Math.max(minVector.y, maxVector.y) + align;
            double f = Math.max(minVector.z, maxVector.z) + align;

            newshape[0] = Shapes.or(newshape[0], Shapes.box(a, b, c, d, e, f));
        });
        return newshape[0];
    }

    public static VoxelShape rotateShapeDirectional(VoxelShape northShape, Direction direction) {
        int i = switch (direction) {
            case NORTH -> 0;
            case UP, EAST -> 3;
            case SOUTH -> 2;
            case DOWN, WEST -> 1;
        };
        if (direction == Direction.UP || direction == Direction.DOWN) return rotateShape(northShape, Direction.Axis.X, i);
        return rotateShape(northShape, Direction.Axis.Y, i);
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
        VoxelShape leg1 = Block.box(0, 0, 13, 3, 3, 16);
        VoxelShape leg2 = Block.box(13, 0, 13, 16, 3, 16);
        VoxelShape body = Block.box(0, 3, 0, 16, 9, 16);
        VoxelShape shape = rotateShapeDirectional(Shapes.or(body, leg1, leg2), direction);
        return doubleBlock(direction, foot, shape, rotateShape(shape, Direction.Axis.Y, 2));
    }

    public static VoxelShape door(Direction direction, boolean bottom) {
        VoxelShape shape = rotateShapeDirectional(Block.box(0, 0, 13, 16, 16, 16), direction);
        return doubleHighSameShape(bottom, shape);
    }

    public static VoxelShape smallDripleaf(boolean bottom) {
        return doubleHighSameShape(bottom, Block.box(2, 0, 2, 14, 13, 14));
    }

    public static VoxelShape tallSeaGrass(boolean bottom) {
        return doubleHighSameShape(bottom, Block.box(2, 0, 2, 14, 16, 14));
    }

    public static VoxelShape piston(Direction direction, boolean isHead, boolean wideArm) {
        double d = wideArm ? 1 : 6;
        VoxelShape head = rotateShapeDirectional(Shapes.or(Block.box(d, d, 4, 16 - d, 16 - d, 20), Block.box(0, 0, 0, 16, 16, 4)), direction);
        VoxelShape base = rotateShapeDirectional(Block.box(0, 0, 4, 16, 16, 16), direction);

        return doubleBlock(direction.getOpposite(), isHead, head, base);
    }

    public static VoxelShape chest(Direction direction) {
        return extendShape(direction, Block.box(1, 0, 1, 15, 14, 15));
    }

    public static VoxelShape pitcherCrop(boolean isBottom, int age) {
        VoxelShape fullUpper = Block.box(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);
        VoxelShape fullLower = Block.box(3.0, -1.0, 3.0, 13.0, 16.0, 13.0);
        VoxelShape bulb = Block.box(5.0, -1.0, 5.0, 11.0, 3.0, 11.0);
        VoxelShape[] upperShapeByAge = new VoxelShape[]{Shapes.empty(), Shapes.empty(), Shapes.empty(), Block.box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0), fullUpper};
        VoxelShape[] lowerShapeByAge = new VoxelShape[]{bulb, Block.box(3.0, -1.0, 3.0, 13.0, 14.0, 13.0), fullLower, fullLower, fullLower};

        return doubleBlock(Direction.UP, isBottom, lowerShapeByAge[age], upperShapeByAge[age]);
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
        VoxelShape[] shape = new VoxelShape[]{Block.box(3, 0, 3, 13, 8, 13), Block.box(3, 0, 3, 13, 10, 13), Block.box(2, 0, 2, 14, 12, 14), Block.box(1, 0, 1, 15, 16, 15)};

        if (topAge == -1) {
            return shape[bottomAge];
        }
        return doubleBlock(Direction.UP, isBottom, shape[3], shape[topAge]);
    }

    public static VoxelShape slidingDoor(Direction direction, boolean isBottom, boolean hingeRight, boolean folding) {
        VoxelShape right = rotateShapeDirectional(folding ? Block.box(13.0, 0.0, 7.0, 19.0, 16.0, 16.0) : Block.box(13, 0, 13, 29, 16, 16), direction);
        VoxelShape left = rotateShapeDirectional(folding ? Block.box(-3.0, 0.0, 7.0, 3.0, 16.0, 16.0) : Block.box(-13, 0, 13, 3, 16, 16), direction);
        return doubleHighSameShape(isBottom, hingeRight ? right : left);
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
        return rotateShapeDirectional(Block.box(0, 3, 6, 16, 6, 16), direction);
    }

    public static VoxelShape fenceHammock(Direction direction) {
        return rotateShapeDirectional(Block.box(0, 3, -2, 16, 6, 16), direction);
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
