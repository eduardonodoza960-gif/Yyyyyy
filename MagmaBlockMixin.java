package com.imdd.reliquias.mixin;

import com.imdd.reliquias.util.ExplorerMagmaManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {
    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void imdd$unavoidableExplorerMagmaDamage(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (!world.isClient && world instanceof ServerWorld serverWorld && ExplorerMagmaManager.isExplorerMagma(world, pos)) {
            if (entity instanceof LivingEntity living) {
                living.damage(serverWorld, serverWorld.getDamageSources().magic(), 2.0f);
                ci.cancel();
            }
        }
    }
}
