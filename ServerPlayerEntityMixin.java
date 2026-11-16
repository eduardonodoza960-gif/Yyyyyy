package com.imdd.reliquias.mixin;

import com.imdd.reliquias.registry.RelicFactory;
import com.imdd.reliquias.registry.RelicType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    // O ocultamento visual em 2ª/3ª pessoa é feito no ArmorFeatureRendererMixin;
    // para outros jogadores no multiplayer, interceptamos também no client renderer de qualquer player usando a bota.
}
