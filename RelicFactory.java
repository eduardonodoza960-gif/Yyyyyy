package com.imdd.reliquias.registry;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class RelicFactory {
    public static final String RELIC_TAG = "imdd_relic_id";

    public static boolean isRelic(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        return customData != null && customData.copyNbt().contains(RELIC_TAG);
    }

    public static RelicType getRelicType(ItemStack stack) {
        if (!isRelic(stack)) return null;
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return null;
        return RelicType.fromId(customData.copyNbt().getString(RELIC_TAG));
    }

    private static MutableText styledName(String text, Formatting color) {
        return Text.literal(text).setStyle(
                Style.EMPTY
                        .withColor(color)
                        .withBold(true)
                        .withItalic(true)
                        .withUnderline(true)
        );
    }

    private static ItemStack baseRelic(Item item, RelicType type, String name, Formatting color) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponentTypes.CUSTOM_NAME, styledName(name, color));
        stack.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(false));
        stack.set(DataComponentTypes.DAMAGE_RESISTANT, new net.minecraft.component.type.DamageResistantComponent(net.minecraft.registry.tag.DamageTypeTags.IS_FIRE));

        NbtCompound nbt = new NbtCompound();
        nbt.putString(RELIC_TAG, type.getId());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return stack;
    }

    private static void addEnchantment(ItemStack stack, DynamicRegistryManager registries, RegistryKey<Enchantment> key, int level, boolean showInTooltip) {
        var lookup = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry<Enchantment> entry = lookup.getOrThrow(key);
        ItemEnchantmentsComponent current = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(current);
        builder.add(entry, level);
        ItemEnchantmentsComponent built = builder.build();
        stack.set(DataComponentTypes.ENCHANTMENTS, built.withShowInTooltip(showInTooltip));
    }

    public static List<ItemStack> createAllRelics(DynamicRegistryManager registries) {
        List<ItemStack> list = new ArrayList<>();

        ItemStack s0 = baseRelic(Items.STICK, RelicType.VELOCIDADE, "Relíquia da Velocidade", Formatting.AQUA);
        addEnchantment(s0, registries, Enchantments.KNOCKBACK, 10, true);
        list.add(s0);

        ItemStack s1 = baseRelic(Items.LEATHER_LEGGINGS, RelicType.FURTIVIDADE, "Relíquia da Furtividade", Formatting.DARK_PURPLE);
        s1.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0x5E2D79, false));
        addEnchantment(s1, registries, Enchantments.PROTECTION, 10, true);
        addEnchantment(s1, registries, Enchantments.SWIFT_SNEAK, 5, true);
        list.add(s1);

        ItemStack s2 = baseRelic(Items.DIAMOND_AXE, RelicType.LENHADOR, "Relíquia do Lenhador", Formatting.AQUA);
        addEnchantment(s2, registries, Enchantments.EFFICIENCY, 10, true);
        addEnchantment(s2, registries, Enchantments.SHARPNESS, 5, true);
        list.add(s2);

        ItemStack s3 = baseRelic(Items.NETHERITE_PICKAXE, RelicType.MINERACAO, "Relíquia da Mineração", Formatting.DARK_PURPLE);
        addEnchantment(s3, registries, Enchantments.EFFICIENCY, 10, true);
        addEnchantment(s3, registries, Enchantments.FORTUNE, 5, true);
        list.add(s3);

        ItemStack s4 = baseRelic(Items.CROSSBOW, RelicType.ARQUEIRO, "Relíquia do Arqueiro", Formatting.RED);
        addEnchantment(s4, registries, Enchantments.QUICK_CHARGE, 5, true);
        addEnchantment(s4, registries, Enchantments.MULTISHOT, 1, true);
        addEnchantment(s4, registries, Enchantments.PIERCING, 10, true);
        list.add(s4);

        ItemStack s5 = baseRelic(Items.RABBIT_FOOT, RelicType.SORTE, "Relíquia da Sorte", Formatting.GREEN);
        s5.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        list.add(s5);

        ItemStack s6 = baseRelic(Items.NETHERITE_SWORD, RelicType.GUERRA, "Relíquia da Guerra", Formatting.DARK_RED);
        addEnchantment(s6, registries, Enchantments.SHARPNESS, 10, true);
        addEnchantment(s6, registries, Enchantments.FIRE_ASPECT, 5, true);
        addEnchantment(s6, registries, Enchantments.LOOTING, 5, true);
        list.add(s6);

        ItemStack s7 = baseRelic(Items.SHIELD, RelicType.DEFESA, "Relíquia da Defesa", Formatting.GOLD);
        addEnchantment(s7, registries, Enchantments.PROTECTION, 10, true);
        addEnchantment(s7, registries, Enchantments.THORNS, 5, true);
        list.add(s7);

        ItemStack s8 = baseRelic(Items.ELYTRA, RelicType.FIM, "Relíquia do Fim", Formatting.YELLOW);
        addEnchantment(s8, registries, Enchantments.PROTECTION, 15, true);
        list.add(s8);

        ItemStack s9 = baseRelic(Items.LEATHER_LEGGINGS, RelicType.INVASOR, "Relíquia do Invasor", Formatting.GRAY);
        s9.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0x474F52, false));
        addEnchantment(s9, registries, Enchantments.BINDING_CURSE, 1, true);
        addEnchantment(s9, registries, Enchantments.VANISHING_CURSE, 1, true);
        addEnchantment(s9, registries, Enchantments.PROTECTION, 8, true);
        list.add(s9);

        ItemStack s10 = baseRelic(Items.TOTEM_OF_UNDYING, RelicType.IMORTALIDADE, "Relíquia da Imortalidade", Formatting.GREEN);
        s10.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        list.add(s10);

        ItemStack s11 = baseRelic(Items.TRIDENT, RelicType.OCEANO, "Relíquia do Oceano", Formatting.AQUA);
        addEnchantment(s11, registries, Enchantments.CHANNELING, 1, true);
        addEnchantment(s11, registries, Enchantments.SHARPNESS, 5, true);
        addEnchantment(s11, registries, Enchantments.IMPALING, 10, true);
        addEnchantment(s11, registries, Enchantments.LOOTING, 3, true);
        addEnchantment(s11, registries, Enchantments.LOYALTY, 3, true);
        list.add(s11);

        ItemStack s12 = baseRelic(Items.DIAMOND_BOOTS, RelicType.EXPLORADOR, "Relíquia do Explorador", Formatting.YELLOW);
        addEnchantment(s12, registries, Enchantments.FROST_WALKER, 2, true);
        list.add(s12);

        ItemStack s13 = baseRelic(Items.COOKIE, RelicType.ESPIAO, "Relíquia do Espião", Formatting.GOLD);
        s13.remove(DataComponentTypes.FOOD);
        s13.remove(DataComponentTypes.CONSUMABLE);
        list.add(s13);

        ItemStack s14 = baseRelic(Items.WIND_CHARGE, RelicType.DEUS_DO_VENTO, "Relíquia do Deus do Vento", Formatting.WHITE);
        list.add(s14);

        ItemStack s15 = baseRelic(Items.RECOVERY_COMPASS, RelicType.WARDEN, "Relíquia do Warden", Formatting.AQUA);
        list.add(s15);

        ItemStack s16 = baseRelic(Items.MACE, RelicType.STOMPER, "Relíquia do Stomper", Formatting.DARK_GRAY);
        addEnchantment(s16, registries, Enchantments.DENSITY, 5, false);
        addEnchantment(s16, registries, Enchantments.BREACH, 4, false);
        addEnchantment(s16, registries, Enchantments.WIND_BURST, 3, false);
        addEnchantment(s16, registries, Enchantments.FIRE_ASPECT, 2, false);
        addEnchantment(s16, registries, Enchantments.SMITE, 5, false);
        addEnchantment(s16, registries, Enchantments.BANE_OF_ARTHROPODS, 5, false);
        list.add(s16);

        ItemStack s17 = baseRelic(Items.DRAGON_EGG, RelicType.DRAGAO, "Relíquia do Dragão", Formatting.DARK_PURPLE);
        list.add(s17);

        ItemStack s18 = baseRelic(Items.LEATHER_BOOTS, RelicType.INVISIBILIDADE, "Relíquia da Invisibilidade", Formatting.WHITE);
        s18.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0x9D9D97, false));
        addEnchantment(s18, registries, Enchantments.PROTECTION, 10, true);
        list.add(s18);

        ItemStack s19 = baseRelic(Items.GOLDEN_HELMET, RelicType.NETHER, "Relíquia do Nether", Formatting.GOLD);
        addEnchantment(s19, registries, Enchantments.PROTECTION, 8, true);
        list.add(s19);

        return list;
    }
}
