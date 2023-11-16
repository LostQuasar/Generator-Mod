package net.furia.generator;

import net.fabricmc.api.ModInitializer;

import net.furia.generator.block.ModBlocks;
import net.furia.generator.block.entity.ModBlockEntities;
import net.furia.generator.recipe.ModRecipes;
import net.furia.generator.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratorMod implements ModInitializer {
	public static final String MOD_ID = "generator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		// register item group
		ModScreenHandlers.registerScreenHandler();
		ModRecipes.registerRecipes();
	}
}