package com.imdd.reliquias.mixin;

import com.imdd.reliquias.registry.RelicFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getStack();
    @Shadow private int itemAge;

    @Inject(method = "tick", at = @At("HEAD"))
    private void imdd$preventDespawn(CallbackInfo ci) {
        if (RelicFactory.isRelic(this.getStack())) {
            this.itemAge = 0;
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void imdd$indestructibleItemEntity(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (RelicFactory.isRelic(this.getStack())) {
            cir.setReturnValue(false);
        }
    }
}
