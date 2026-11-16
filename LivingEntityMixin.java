package com.imdd.reliquias.mixin;

import com.imdd.reliquias.event.RelicEventHandler;
import com.imdd.reliquias.registry.RelicFactory;
import com.imdd.reliquias.registry.RelicType;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void imdd$cancelFallDamageWithStomper(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (source.isOf(DamageTypes.FALL) && RelicEventHandler.isHoldingRelic(self, RelicType.STOMPER)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tryUseDeathProtector", at = @At("HEAD"), cancellable = true)
    private void imdd$customImmortalityTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayerEntity player)) return;

        Hand foundHand = null;
        ItemStack foundStack = ItemStack.EMPTY;

        for (Hand hand : Hand.values()) {
            ItemStack inHand = player.getStackInHand(hand);
            if (RelicFactory.getRelicType(inHand) == RelicType.IMORTALIDADE) {
                foundHand = hand;
                foundStack = inHand;
                break;
            }
        }

        if (foundHand == null) return;

        if (player.getItemCooldownManager().isCoolingDown(foundStack)) {
            cir.setReturnValue(false);
            return;
        }

        player.incrementStat(Stats.USED.getOrCreateStat(foundStack.getItem()));
        Criteria.USED_TOTEM.trigger(player, foundStack);

        player.setHealth(1.0f);
        player.clearStatusEffects();
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
        player.getWorld().sendEntityStatus(player, EntityStatuses.USE_TOTEM_OF_UNDYING);

        player.setStackInHand(foundHand, foundStack);
        player.getItemCooldownManager().set(foundStack, 200);

        cir.setReturnValue(true);
    }
}
