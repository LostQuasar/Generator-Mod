package net.furia.generator.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.furia.generator.GeneratorMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    //public static final Item NUGGIE = registerItem("nuggie", new NuggetFoodItem(new FabricItemSettings().food(ModFoodComponents.NUGGIE)));

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(GeneratorMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        GeneratorMod.LOGGER.info("Registering Mod Items...");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(ModItems::itemGroupFoodAndDrink);
    }

    private static void itemGroupFoodAndDrink(FabricItemGroupEntries entries) {
    }
}
