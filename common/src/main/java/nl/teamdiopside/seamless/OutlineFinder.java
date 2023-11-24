package nl.teamdiopside.seamless;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static nl.teamdiopside.seamless.Reload.RULES;

public class OutlineFinder {
    public record Recursion(VoxelShape voxelShape, Set<BlockPos> connectedPositions) {}

    public static Recursion findAndAddShapes(Level level, BlockState state, BlockPos pos, Set<BlockPos> connectedPositions, BlockPos originalPos, Entity entity) {
        connectedPositions.add(pos);
        BlockPos relativePos = pos.subtract(originalPos);
        VoxelShape shape = state.getShape(level, pos, CollisionContext.of(entity)).move(relativePos.getX(), relativePos.getY(), relativePos.getZ());

        if (connectedPositions.size() > 1500) {
            return new Recursion(shape, connectedPositions);
        }

        for (Reload.OutlineRule outlineRule : RULES) {
            ResourceLocation location = outlineRule.location();

            if (blockDoesntMatch(outlineRule.blocks(), state.getBlock(), null, location)) {
                continue;
            }

            boolean blockstatesMatch = true;
            for (HashMap.Entry<String, Set<String>> entry : outlineRule.blockstates().entrySet()) {
                if (propertyDoesntMatch(state, entry.getKey(), entry.getValue(), null, location)) {
                    blockstatesMatch = false;
                }
            }
            if (!blockstatesMatch) {
                continue;
            }

            for (Direction direction : getDirections(outlineRule.directions(), location, state)) {
                BlockPos checkingPos = pos.relative(direction);
                BlockState checkingState = level.getBlockState(checkingPos);

                if (connectedPositions.contains(checkingPos) || blockDoesntMatch(outlineRule.connectingBlocks(), checkingState.getBlock(), state.getBlock(), location)) {
                    continue;
                }

                boolean connectingBlockstatesMatch = true;
                for (HashMap.Entry<String, Set<String>> entry : outlineRule.connectingBlockstates().entrySet()) {
                    if (propertyDoesntMatch(checkingState, entry.getKey(), entry.getValue(), state, location)) {
                        connectingBlockstatesMatch = false;
                        break;
                    }
                }
                if (!connectingBlockstatesMatch) {
                    continue;
                }

                Recursion recursion = findAndAddShapes(level, checkingState, checkingPos, connectedPositions, originalPos, entity);
                shape = Shapes.joinUnoptimized(shape, recursion.voxelShape(), BooleanOp.OR);
                connectedPositions = recursion.connectedPositions();
            }
        }
        return new Recursion(shape, connectedPositions);
    }

    public static boolean blockDoesntMatch(Set<String> set, Block checkingBlock, Block originalBlock, ResourceLocation location) {
        Set<Block> goodBlocks = new HashSet<>();
        Set<Block> nonoBlocks = new HashSet<>();

        for (String string : set) {
            if (originalBlock != null) {
                if (string.equals("/same")) {
                    goodBlocks.add(originalBlock);
                } else if (string.equals("/!same")) {
                    nonoBlocks.add(originalBlock);
                }
            }

            if (string.startsWith("!")) {
                nonoBlocks.addAll(getBlocks(string.substring(1), location));
            } else {
                goodBlocks.addAll(getBlocks(string, location));
            }
        }

        return !goodBlocks.contains(checkingBlock) || nonoBlocks.contains(checkingBlock);
    }

    public static Set<Block> getBlocks(String string, ResourceLocation location) {
        Set<Block> blocks = new HashSet<>();

        if (string.startsWith("#")) {
            TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(string.replace("#", "")));
            BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder -> blocks.add(blockHolder.value()));
        } else {
            if (!Seamless.modIds.contains(string.replace("#", "").split(":")[0])) {
                return blocks;
            }

            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(string));
            if (block == Blocks.AIR && !string.split(":")[1].equals("air")) {
                initialError("Block \"" + string + "\" from " + location + " does not exist!");
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static boolean propertyDoesntMatch(BlockState checkingState, String propertyName, Set<String> values, BlockState originalState, ResourceLocation location) {
        Property<?> checkingProperty = checkingState.getBlock().getStateDefinition().getProperty(propertyName);
        if (checkingProperty == null) {
            initialError("Blockstate property \"" + propertyName + "\" from " + location + " does not exist for " + checkingState.getBlock().getName());
            return true;
        }

        String valueName = checkingState.getValue(checkingProperty).toString();
        if (checkingState.getValue(checkingProperty) instanceof StringRepresentable representable) {
            valueName = representable.getSerializedName();
        }

        Set<String> goodValues = new HashSet<>(Set.copyOf(values));
        Set<String> nonoValues = new HashSet<>();
        boolean useNono = false;

        if (originalState != null) {
            for (String value : values) {
                if (!(value.startsWith("/same") || value.startsWith("/!same"))) {
                    continue;
                }

                String addToPropertyString = value.contains("+") ? value.split("\\+")[1] : "0";
                int addToProperty = 0;

                if (!addToPropertyString.equals("opposite")) {
                    try {
                        addToProperty = Integer.parseInt(addToPropertyString);
                    } catch (NumberFormatException e) {
                        throw (new NumberFormatException("Blockstate \"" + value + "\" from " + location + " does not exist because \"" + addToPropertyString + "\" is not an integer"));
                    }
                }

                Property<?> originalProperty = originalState.getBlock().getStateDefinition().getProperty(propertyName);
                if (originalProperty == null) {
                    initialError("Blockstate property \"" + propertyName + "\" from " + location + " does not exist for " + originalState.getBlock().getName());
                    continue;
                }

                Set<String> toAdd = new HashSet<>();
                if (originalProperty instanceof DirectionProperty directionProperty) {
                    Direction direction = originalState.getValue(directionProperty);
                    for (int i = 0; i < addToProperty; i++) {
                        direction = direction.getClockWise();
                    }
                    if (addToPropertyString.equals("opposite")) {
                        direction = direction.getOpposite();
                    }
                    toAdd.add(direction.getName());
                }
                else if (originalProperty == BlockStateProperties.AXIS) {
                    toAdd.add(Direction.fromAxisAndDirection(originalState.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.NEGATIVE).toString());
                    toAdd.add(Direction.fromAxisAndDirection(originalState.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE).toString());
                }
                else if (originalProperty == BlockStateProperties.HORIZONTAL_AXIS) {
                    toAdd.add(Direction.fromAxisAndDirection(originalState.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.NEGATIVE).toString());
                    toAdd.add(Direction.fromAxisAndDirection(originalState.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE).toString());
                }
                else if (originalProperty instanceof IntegerProperty integerProperty) {
                    toAdd.add(String.valueOf(originalState.getValue(integerProperty) + addToProperty));
                } else {
                    toAdd.add(String.valueOf(originalState.getValue(originalProperty)));
                }

                if (value.startsWith("/same")) {
                    goodValues.addAll(toAdd);
                } else if (value.startsWith("/!same")) {
                    useNono = true;
                    nonoValues.addAll(toAdd);
                }
            }
        }

        boolean propertiesMatch = propertyName.equals(checkingProperty.getName());
        boolean valuesMatch = goodValues.contains(valueName) || (!nonoValues.contains(valueName) && useNono);

        return !propertiesMatch || !valuesMatch;
    }

    public static Set<Direction> getDirections(Set<String> set, ResourceLocation location, BlockState state) {
        Set<Direction> directions = new HashSet<>();
        for (String string : set) {
            if (string.startsWith("/state:")) {
                String propertyString = string.split(":")[1].split("\\+")[0];
                String addToPropertyString = string.contains("+") ? string.split("\\+")[1] : "0";
                int addToProperty = 0;

                if (!addToPropertyString.equals("opposite")) {
                    try {
                        addToProperty = Integer.parseInt(addToPropertyString);
                    } catch (NumberFormatException e) {
                        initialError("Direction \"" + string + "\" from " + location + " does not exist because \"" + addToPropertyString + "\" is not an integer");
                    }
                }

                Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyString);
                if (property == null) {
                    initialError("Blockstate property \"" + propertyString + "\" from " + location + " does not exist for " + state.getBlock().getName());
                    continue;
                }

                if (property instanceof DirectionProperty directionProperty) {
                    Direction direction = state.getValue(directionProperty);
                    for (int i = 0; i < addToProperty; i++) {
                        direction = direction.getClockWise();
                    }
                    if (addToPropertyString.equals("opposite")) {
                        direction = direction.getOpposite();
                    }
                    directions.add(direction);
                }
                else if (property == BlockStateProperties.AXIS) {
                    directions.add(Direction.fromAxisAndDirection(state.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.NEGATIVE));
                    directions.add(Direction.fromAxisAndDirection(state.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE));
                }
                else if (property == BlockStateProperties.HORIZONTAL_AXIS) {
                    directions.add(Direction.fromAxisAndDirection(state.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.NEGATIVE));
                    directions.add(Direction.fromAxisAndDirection(state.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE));
                } else {
                    initialError("Property \"" + propertyString + "\" from " + location + "\" is not a direction property");
                }
            } else {
                Direction direction = Direction.byName(string);
                if (direction != null) {
                    directions.add(direction);
                } else {
                    initialError("Direction \"" + string + "\" from " + location + " does not exist!");
                }
            }
        }
        return directions;
    }
    
    public static void initialError(String string) {
        if (!Seamless.errors.contains(string)) {
            Seamless.LOGGER.error(string);
            Seamless.errors.add(string);
        }
    }
}
