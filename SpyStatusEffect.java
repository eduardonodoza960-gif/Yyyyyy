package com.imdd.reliquias.effect;

import com.imdd.reliquias.ImddReliquiasMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class SpyStatusEffect extends StatusEffect {
    public static final Identifier SCALE_MODIFIER_ID = Identifier.of(ImddReliquiasMod.MOD_ID, "spy_scale");

    public SpyStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
        this.addAttributeModifier(
                EntityAttributes.SCALE,
                SCALE_MODIFIER_ID,
                -0.9,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
