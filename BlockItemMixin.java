package com.imdd.reliquias.mixin;

import com.imdd.reliquias.event.RelicEventHandler;
import com.imdd.reliquias.registry.RelicFactory;
import com.imdd.reliquias.registry.RelicType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void imdd$preventDragonEggPlacementAndTeleport(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        if (RelicFactory.getRelicType(stack) == RelicType.DRAGAO) {
            if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
                RelicEventHandler.teleportRandomly(serverPlayer);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
