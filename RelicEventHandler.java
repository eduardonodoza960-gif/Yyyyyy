package com.imdd.reliquias.event;

import com.imdd.reliquias.effect.ModEffects;
import com.imdd.reliquias.registry.RelicFactory;
import com.imdd.reliquias.registry.RelicType;
import com.imdd.reliquias.util.ExplorerMagmaManager;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelicEventHandler {
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            RelicType type = RelicFactory.getRelicType(stack);
            if (type == null) return ActionResult.PASS;

            if (world.isClient) {
                if (type == RelicType.ESPIAO || type == RelicType.DEUS_DO_VENTO || type == RelicType.WARDEN || type == RelicType.DRAGAO) {
                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            switch (type) {
                case ESPIAO -> {
                    if (serverPlayer.getItemCooldownManager().isCoolingDown(stack)) {
                        return ActionResult.FAIL;
                    }
                    serverPlayer.getItemCooldownManager().set(stack, 10800);
                    serverPlayer.addStatusEffect(new StatusEffectInstance(ModEffects.SPY, 9600, 1, false, false, true));
                    serverPlayer.getServerWorld().playSound(null, serverPlayer.getBlockPos(), SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 0.8f, 1.3f);
                    return ActionResult.SUCCESS;
                }
                case DEUS_DO_VENTO -> {
                    ServerWorld sw = serverPlayer.getServerWorld();
                    Vec3d look = serverPlayer.getRotationVec(1.0f);
                    for (int i = 0; i < 2; i++) {
                        WindChargeEntity windCharge = new WindChargeEntity(serverPlayer, sw, serverPlayer.getX(), serverPlayer.getEyeY() - 0.1, serverPlayer.getZ());
                        double offset = (i == 0) ? -0.04 : 0.04;
                        windCharge.setVelocity(look.x + offset, look.y, look.z - offset, 1.5f, 1.0f);
                        sw.spawnEntity(windCharge);
                    }
                    sw.playSound(null, serverPlayer.getBlockPos(), SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.PLAYERS, 0.7f, 1.0f);
                    return ActionResult.SUCCESS;
                }
                case WARDEN -> {
                    if (serverPlayer.getItemCooldownManager().isCoolingDown(stack)) {
                        return ActionResult.FAIL;
                    }
                    serverPlayer.getItemCooldownManager().set(stack, 200);
                    fireSonicBoom(serverPlayer);
                    return ActionResult.SUCCESS;
                }
                case DRAGAO -> {
                    teleportRandomly(serverPlayer);
                    return ActionResult.SUCCESS;
                }
                default -> {
                    return ActionResult.PASS;
                }
            }
        });
    }

    public static void tickPlayers(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (hasRelicInInventory(player, RelicType.SORTE)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 40, 4, true, false, true));
            }

            ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
            if (RelicFactory.getRelicType(legs) == RelicType.INVASOR) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 25, 4, true, false, true));
            }

            ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);
            RelicType feetType = RelicFactory.getRelicType(feet);
            if (feetType == RelicType.EXPLORADOR) {
                ExplorerMagmaManager.convertLavaAroundPlayer(player);
            }

            if (feetType == RelicType.INVISIBILIDADE) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 85, 12, true, false, true));
            }

            ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
            if (RelicFactory.getRelicType(head) == RelicType.NETHER) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 85, 4, true, false, true));
            }

            if (isHoldingRelic(player, RelicType.STOMPER)) {
                player.fallDistance = 0.0f;
            }
        }
    }

    public static boolean hasRelicInInventory(ServerPlayerEntity player, RelicType target) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (RelicFactory.getRelicType(player.getInventory().getStack(i)) == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHoldingRelic(LivingEntity entity, RelicType target) {
        return RelicFactory.getRelicType(entity.getMainHandStack()) == target
                || RelicFactory.getRelicType(entity.getOffHandStack()) == target;
    }

    private static void fireSonicBoom(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Vec3d start = player.getEyePos();
        Vec3d dir = player.getRotationVec(1.0f).normalize();
        double range = 24.0;

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 2.0f, 1.0f);

        Set<Entity> hitEntities = new HashSet<>();
        for (double d = 1.0; d <= range; d += 1.0) {
            Vec3d point = start.add(dir.multiply(d));
            world.spawnParticles(ParticleTypes.SONIC_BOOM, point.x, point.y, point.z, 1, 0, 0, 0, 0);

            Box box = new Box(point.subtract(1.2, 1.2, 1.2), point.add(1.2, 1.2, 1.2));
            List<Entity> found = world.getOtherEntities(player, box, e -> e instanceof LivingEntity && e.isAlive());
            hitEntities.addAll(found);
        }

        for (Entity entity : hitEntities) {
            if (entity instanceof LivingEntity target) {
                DamageSource sonicSource = world.getDamageSources().sonicBoom(player);
                target.damage(world, sonicSource, 5.0f);

                Vec3d knockback = new Vec3d(dir.x * 2.4, 0.55, dir.z * 2.4);
                target.setVelocity(knockback);
                target.velocityModified = true;
            }
        }
    }

    public static void teleportRandomly(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        double originX = player.getX();
        double originY = player.getY();
        double originZ = player.getZ();

        for (int attempt = 0; attempt < 16; attempt++) {
            double targetX = originX + (player.getRandom().nextDouble() - 0.5) * 32.0;
            double targetZ = originZ + (player.getRandom().nextDouble() - 0.5) * 32.0;
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MathHelper.floor(targetX), MathHelper.floor(targetZ));
            double targetY = MathHelper.clamp(originY + (player.getRandom().nextInt(16) - 8), world.getBottomY() + 2, Math.max(topY, world.getBottomY() + 2));

            BlockPos dest = BlockPos.ofFloored(targetX, targetY, targetZ);
            if (world.getBlockState(dest).isAir() && world.getBlockState(dest.up()).isAir() && !world.getBlockState(dest.down()).isAir()) {
                world.spawnParticles(ParticleTypes.PORTAL, originX, originY + 1.0, originZ, 40, 0.5, 1.0, 0.5, 0.1);
                world.playSound(null, originX, originY, originZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

                player.requestTeleport(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5);

                world.spawnParticles(ParticleTypes.PORTAL, dest.getX() + 0.5, dest.getY() + 1.0, dest.getZ() + 0.5, 40, 0.5, 1.0, 0.5, 0.1);
                world.playSound(null, dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return;
            }
        }
    }
}
