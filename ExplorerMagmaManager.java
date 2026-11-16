package com.imdd.reliquias.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExplorerMagmaManager {
    private static final int LIFETIME_TICKS = 800;

    private record MagmaKey(RegistryKey<World> dimension, BlockPos pos) {}

    private static class MagmaEntry {
        int remainingTicks;
        final int breakerId;

        MagmaEntry(int remainingTicks, int breakerId) {
            this.remainingTicks = remainingTicks;
            this.breakerId = breakerId;
        }
    }

    private static final Map<MagmaKey, MagmaEntry> ACTIVE_MAGMA = new HashMap<>();
    private static int nextBreakerId = 200000;

    public static boolean isExplorerMagma(World world, BlockPos pos) {
        return ACTIVE_MAGMA.containsKey(new MagmaKey(world.getRegistryKey(), pos.toImmutable()));
    }

    public static void convertLavaAroundPlayer(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = player.getBlockPos().down();

        for (int dx = -3; dx <= 2; dx++) {
            for (int dz = -3; dz <= 2; dz++) {
                BlockPos target = center.add(dx, 0, dz);
                BlockState state = world.getBlockState(target);
                BlockState above = world.getBlockState(target.up());

                if (state.isOf(Blocks.LAVA) && above.isAir()) {
                    world.setBlockState(target, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
                    MagmaKey key = new MagmaKey(world.getRegistryKey(), target.toImmutable());
                    ACTIVE_MAGMA.put(key, new MagmaEntry(LIFETIME_TICKS, nextBreakerId++));
                }
            }
        }
    }

    public static void tick(MinecraftServer server) {
        if (ACTIVE_MAGMA.isEmpty()) return;

        Iterator<Map.Entry<MagmaKey, MagmaEntry>> it = ACTIVE_MAGMA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<MagmaKey, MagmaEntry> entry = it.next();
            MagmaKey key = entry.getKey();
            MagmaEntry data = entry.getValue();

            ServerWorld world = server.getWorld(key.dimension());
            if (world == null) {
                it.remove();
                continue;
            }

            if (!world.getBlockState(key.pos()).isOf(Blocks.MAGMA_BLOCK)) {
                world.setBlockBreakingInfo(data.breakerId, key.pos(), -1);
                it.remove();
                continue;
            }

            data.remainingTicks--;
            if (data.remainingTicks <= 0) {
                world.setBlockBreakingInfo(data.breakerId, key.pos(), -1);
                world.setBlockState(key.pos(), Blocks.LAVA.getDefaultState(), 3);
                it.remove();
            } else {
                int elapsed = LIFETIME_TICKS - data.remainingTicks;
                int stage = (elapsed * 10) / LIFETIME_TICKS;
                world.setBlockBreakingInfo(data.breakerId, key.pos(), stage);
            }
        }
    }
}
