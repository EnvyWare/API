package com.envyful.api.forge.world;

import com.envyful.api.math.UtilRandom;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Static utility class for world methods
 *
 */
public class UtilWorld {

    /**
     *
     * Gets a random position in the world
     * Returns the BlockPos selected
     *
     * @param world The name of the world
     * @param radius the x and z radius distance from 0
     * @return The BlockPos found to be valid/safe
     */
    public static BlockPos getRandomPosition(World world, int radius) {
        return getRandomPosition(world, radius, radius);
    }

    /**
     *
     * Gets a random position in the world
     * Returns the BlockPos selected
     *
     * @param world The name of the world
     * @param radiusX the radius distance from 0
     * @param RadiusZ the radius distance from 0
     * @return The BlockPos found to be valid/safe
     */
    public static BlockPos getRandomPosition(World world, int radiusX, int RadiusZ) {
        BlockPos pos = null;
        int y = -1;
        while (pos == null || y == -1) {
            pos = getRandomXAndZPosition(radiusX, RadiusZ);

            if (world.dimensionType().hasCeiling()) {
                y = getNetherYPosition(world, pos);
            } else {
                y = world.getChunk(pos).getHeight(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
            }
        }

        return new BlockPos(pos.getX(), y, pos.getZ());
    }

    private static BlockPos getRandomXAndZPosition(int radiusX, int radiusZ) {
        return new BlockPos(
                (UtilRandom.randomBoolean() ? 1 : -1) * UtilRandom.randomInteger(0, radiusX),
                0,
                (UtilRandom.randomBoolean() ? 1 : -1) * UtilRandom.randomInteger(0, radiusZ));
    }

    private static int getNetherYPosition(World world, BlockPos pos) {
        for (int i = world.getHeight(); i > 5; i--) {
            BlockPos testPos = new BlockPos(pos.getX(), i, pos.getZ());
            if (!world.getBlockState(testPos).getBlock().is(Blocks.BEDROCK) && world.getBlockState(testPos).getBlock().is(Blocks.AIR)) {
                if (world.getBlockState(testPos.below(1)).getBlock().is(Blocks.AIR)) {
                    BlockState groundState = world.getBlockState(testPos.below(2));
                    if (groundState.getMaterial().isSolid() && !groundState.getMaterial().isLiquid()) {
                        return testPos.getY() - 1;
                    }
                }
            }
        }

        return -1;
    }

    /**
     *
     * Finds a world represented by the given name.
     * Returns null if not found
     *
     * @param name The name of the world to be found
     * @return The world found
     */
    public static World findWorld(String name) {
        for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            if (getName(world).equalsIgnoreCase(name)) {
                return world;
            }
        }

        return null;
    }

    public static List<String> getWorldNames() {
        List<String> names = new ArrayList<>();

        for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            names.add(getName(world));
        }

        return names;
    }

    /**
     *
     * Obtains the name of the world and abstracts the impl away from the platform.
     *
     * @param world The world
     * @return The name of the world
     */
    public static String getName(World world) {
        if (!(world instanceof ServerWorld) || !(world.getLevelData() instanceof IServerWorldInfo)) {
            return "NONE";
        }

        return ((IServerWorldInfo) world.getLevelData()).getLevelName();
    }
}
