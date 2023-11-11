package net.furia.generator;

import net.fabricmc.api.ClientModInitializer;
import net.furia.generator.screen.GeneratorScreen;
import net.furia.generator.screen.ModScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class GeneratorModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.GENERATOR_SCREEN_HANDLER, GeneratorScreen::new);

    }
}
