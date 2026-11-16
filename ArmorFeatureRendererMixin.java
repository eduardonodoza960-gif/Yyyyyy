package com.imdd.reliquias.mixin;

import com.imdd.reliquias.registry.RelicFactory;
import com.imdd.reliquias.registry.RelicType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void imdd$hideAllArmorWithInvisibilityRelic(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, S state, float limbAngle, float limbDistance, CallbackInfo ci) {
        if (state instanceof PlayerEntityRenderState playerState && MinecraftClient.getInstance().world != null) {
            var entity = MinecraftClient.getInstance().world.getEntityById(playerState.id);
            if (entity instanceof AbstractClientPlayerEntity player) {
                if (RelicFactory.getRelicType(player.getEquippedStack(EquipmentSlot.FEET)) == RelicType.INVISIBILIDADE) {
                    ci.cancel();
                }
            }
        }
    }
}
