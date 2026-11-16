package com.imdd.reliquias;

import com.imdd.reliquias.command.ReliquiasCommand;
import com.imdd.reliquias.effect.ModEffects;
import com.imdd.reliquias.event.RelicEventHandler;
import com.imdd.reliquias.util.ExplorerMagmaManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImddReliquiasMod implements ModInitializer {
    public static final String MOD_ID = "imdd_reliquias";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEffects.register();
        RelicEventHandler.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ReliquiasCommand.register(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ExplorerMagmaManager.tick(server);
            RelicEventHandler.tickPlayers(server);
        });

        LOGGER.info("IMDD Relíquias 1.21.4 carregado com sucesso!");
    }
}
