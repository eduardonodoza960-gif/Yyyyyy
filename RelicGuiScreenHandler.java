package com.imdd.reliquias.gui;

import com.imdd.reliquias.registry.RelicFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;

import java.util.List;

public class RelicGuiScreenHandler extends GenericContainerScreenHandler {
    private final SimpleInventory relicInventory;

    public RelicGuiScreenHandler(int syncId, PlayerInventory playerInventory, ServerPlayerEntity player) {
        this(syncId, playerInventory, createPopulatedInventory(player));
    }

    private RelicGuiScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory inv) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inv, 3);
        this.relicInventory = inv;
    }

    private static SimpleInventory createPopulatedInventory(ServerPlayerEntity player) {
        SimpleInventory inv = new SimpleInventory(27);
        List<ItemStack> relics = RelicFactory.createAllRelics(player.getRegistryManager());
        for (int i = 0; i < relics.size() && i < 27; i++) {
            inv.setStack(i, relics.get(i));
        }
        return inv;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < 27) {
            ItemStack clicked = this.relicInventory.getStack(slotIndex);
            if (!clicked.isEmpty() && player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack copy = clicked.copy();
                if (!serverPlayer.getInventory().insertStack(copy)) {
                    serverPlayer.dropItem(copy, false);
                }
                serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.8f, 1.2f);
                Text msg = Text.literal("IMDD").formatted(Formatting.DARK_PURPLE, Formatting.BOLD)
                        .append(Text.literal(" >> ").formatted(Formatting.GRAY))
                        .append(Text.literal("Você pegou a relíquia: ").formatted(Formatting.YELLOW))
                        .append(clicked.getName());
                serverPlayer.sendMessage(msg, false);
            }
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
}
