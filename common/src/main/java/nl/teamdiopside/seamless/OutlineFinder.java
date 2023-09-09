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

        if (connectedPositions.size() > 2000) {
            return new Recursion(shape, connectedPositions);
        }

        for (Reload.OutlineRule outlineRule : RULES) {
            ResourceLocation location = outlineRule.location();

            if (!getBlocks(outlineRule.blocks(), null, location).contains(state.getBlock())) {
                continue;
            }

            boolean blockstatesMatch = true;
            for (HashMap.Entry<String, Set<String>> entry : outlineRule.blockstates().entrySet()) {
                if (!propertyMatches(state, entry.getKey(), entry.getValue(), null, location)) {
                    blockstatesMatch = false;
                }
            }
            if (!blockstatesMatch) {
                continue;
            }

            for (Direction direction : getDirections(outlineRule.directions(), location, state)) {
                BlockPos checkingPos = pos.relative(direction);
                BlockState checkingState = level.getBlockState(checkingPos);

                if (connectedPositions.contains(checkingPos) || !getBlocks(outlineRule.connectingBlocks(), state, location).contains(checkingState.getBlock())) {
                    continue;
                }

                boolean connectingBlockstatesMatch = true;
                for (HashMap.Entry<String, Set<String>> entry : outlineRule.connectingBlockstates().entrySet()) {
                    if (!propertyMatches(checkingState, entry.getKey(), entry.getValue(), state, location)) {
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

    public static Set<Block> getBlocks(Set<String> set, BlockState state, ResourceLocation location) {
        Set<Block> blocks = new HashSet<>();

        for (String string : set) {
            if (string.startsWith("/same") && state != null) {
                blocks.add(state.getBlock());
            } else {
                blocks.addAll(getNormalBlocks(string, location));
            }
        }

        return blocks;
    }

    public static Set<Block> getNormalBlocks(String string, ResourceLocation location) {
        Set<Block> blocks = new HashSet<>();
        if (string.startsWith("#")) {
            TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, new ResourceLocation(string.replace("#", "")));
            BuiltInRegistries.BLOCK.getOrCreateTag(blockTagKey).stream().forEach(blockHolder -> blocks.add(blockHolder.value()));
        } else {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(string));
            if (block == Blocks.AIR && !string.split(":")[1].equals("air")) {
                Seamless.LOGGER.error("Block \"" + string + "\" from " + location + " does not exist!");
            } else {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static boolean propertyMatches(BlockState checkingState, String propertyName, Set<String> values, BlockState originalState, ResourceLocation location) {
        Property<?> checkingProperty = checkingState.getBlock().getStateDefinition().getProperty(propertyName);
        assert checkingProperty != null : "Blockstate property " + propertyName + " from " + location + " does not exist for " + checkingState.getBlock().getName();

        String valueName = checkingState.getValue(checkingProperty).toString();
        if (checkingState.getValue(checkingProperty) instanceof StringRepresentable representable) {
            valueName = representable.getSerializedName();
        }

        Set<String> goodValues = new HashSet<>(Set.copyOf(values));
        Set<String> nonoValues = new HashSet<>();
        boolean useNono = false;

        if (originalState != null) {
            for (String value : goodValues) {
                if (!(value.startsWith("/same") || value.startsWith("/!same"))) {
                    continue;
                }

                String addToPropertyString = value.contains("+") ? value.split("\\+")[1] : "0";
                int addToProperty;

                try {
                    addToProperty = Integer.parseInt(addToPropertyString);
                } catch (NumberFormatException e) {
                    throw (new NumberFormatException("Blockstate \"" + value + "\" from " + location + " does not exist because \"" + addToPropertyString + "\" is not an integer"));
                }

                Property<?> originalProperty = originalState.getBlock().getStateDefinition().getProperty(propertyName);
                assert originalProperty != null : "Blockstate property " + propertyName + " from " + location + " does not exist for " + originalState.getBlock().getName();

                String toAdd;
                if (originalProperty instanceof DirectionProperty directionProperty) {
                    Direction direction = originalState.getValue(directionProperty);
                    for (int i = 0; i < addToProperty; i++) {
                        direction = direction.getClockWise();
                    }
                    toAdd = direction.getName();
                } else if (originalProperty instanceof IntegerProperty integerProperty) {
                    toAdd = String.valueOf(originalState.getValue(integerProperty) + addToProperty);
                } else {
                    toAdd = String.valueOf(originalState.getValue(originalProperty));
                }

                if (value.startsWith("/same")) {
                    goodValues.add(toAdd);
                } else if (value.startsWith("/!same")) {
                    useNono = true;
                    nonoValues.add(toAdd);
                }
            }
        }

        boolean propertiesMatch = propertyName.equals(checkingProperty.getName());
        boolean valuesMatch = goodValues.contains(valueName) || (!nonoValues.contains(valueName) && useNono);

        return propertiesMatch && valuesMatch;
    }

    public static Set<Direction> getDirections(Set<String> set, ResourceLocation location, BlockState state) {
        Set<Direction> directions = new HashSet<>();
        for (String string : set) {
            if (string.startsWith("/state:")) {
                String propertyString = string.split(":")[1].split("\\+")[0];
                String addToPropertyString = string.contains("+") ? string.split("\\+")[1] : "0";
                int addToProperty;

                try {
                    addToProperty = Integer.parseInt(addToPropertyString);
                } catch (NumberFormatException e) {
                    throw (new NumberFormatException("Direction \"" + string + "\" from " + location + " does not exist because \"" + addToPropertyString + "\" is not an integer"));
                }

                Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyString);
                assert property != null : "Blockstate property \"" + propertyString + "\" from " + location + " does not exist for " + state.getBlock().getName();

                if (property instanceof DirectionProperty directionProperty) {
                    Direction direction = state.getValue(directionProperty);
                    for (int i = 0; i < addToProperty; i++) {
                        direction = direction.getClockWise();
                    }
                    directions.add(direction);
                } else {
                    Seamless.LOGGER.error("Property \"" + propertyString + "\" from " + location + "\" is not a direction property");
                }
            } else {
                Direction direction = Direction.byName(string);
                if (direction != null) {
                    directions.add(direction);
                } else {
                    Seamless.LOGGER.error("Direction \"" + string + "\" from " + location + " does not exist!");
                }
            }
        }
        return directions;
    }
}
