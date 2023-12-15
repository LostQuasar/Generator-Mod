package net.furia.generator.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.furia.generator.GeneratorMod;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block GENERATOR_BLOCK = registerBlock("generator",
            new GeneratorBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE).nonOpaque()));

    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
    	    content.add(block);
        });
        return Registry.register(Registries.BLOCK, new Identifier(GeneratorMod.MOD_ID, name), block);
    }

    public static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(GeneratorMod.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        GeneratorMod.LOGGER.info("Registering Mod Blocks...");
    }
}
