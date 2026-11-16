package com.imdd.reliquias.command;

import com.imdd.reliquias.gui.RelicGuiScreenHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;

public class ReliquiasCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("reliquias")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    openGui(player);
                    return 1;
                }));

        dispatcher.register(CommandManager.literal("reliquia")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    openGui(player);
                    return 1;
                }));
    }

    public static void openGui(ServerPlayerEntity player) {
        Text title = Text.literal("IMDD").formatted(Formatting.DARK_PURPLE, Formatting.BOLD)
                .append(Text.literal(" >> ").formatted(Formatting.GRAY))
                .append(Text.literal("Relíquia").formatted(Formatting.DARK_GRAY));

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, p) -> new RelicGuiScreenHandler(syncId, inv, player),
                title
        ));
    }
}
