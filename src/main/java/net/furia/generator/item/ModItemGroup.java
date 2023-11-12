package net.furia.generator.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.furia.generator.block.ModBlocks;
import net.minecraft.item.ItemGroups;

public class ModItemGroup {
    public static void registerItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(ModItemGroup::itemGroupRedstone);
    }

    private static void itemGroupRedstone(FabricItemGroupEntries entries) {
        entries.add(ModBlocks.GENERATOR_BLOCK);
    }
}
