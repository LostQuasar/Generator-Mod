package net.furia.generator.recipe;

import net.furia.generator.GeneratorMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(GeneratorMod.MOD_ID, GeneratorRecipes.Serializer.ID),
                GeneratorRecipes.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(GeneratorMod.MOD_ID, GeneratorRecipes.Type.ID),
                GeneratorRecipes.Type.INSTANCE);
    }
}
