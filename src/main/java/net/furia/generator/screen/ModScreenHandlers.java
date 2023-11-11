package net.furia.generator.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.furia.generator.GeneratorMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<GeneratorScreenHandler> GENERATOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(GeneratorMod.MOD_ID, "generator_screen_handler"),
                    new ExtendedScreenHandlerType<>(GeneratorScreenHandler::new));


    public static void registerScreenHandler() {
        GeneratorMod.LOGGER.info("Registering Screen Handlers...");
    }
}
