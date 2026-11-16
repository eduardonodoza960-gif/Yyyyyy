package com.imdd.reliquias.effect;

import com.imdd.reliquias.ImddReliquiasMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static RegistryEntry<StatusEffect> SPY;

    public static void register() {
        SPY = Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(ImddReliquiasMod.MOD_ID, "spy"),
                new SpyStatusEffect()
        );
    }
}
