package net.furia.generator.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.furia.generator.GeneratorMod;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class GeneratorRecipes implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> recipeItems;
    private final int time;

    public GeneratorRecipes(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, int time) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.time = time;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }

        return (recipeItems.get(0).test(inventory.getStack(0)) && recipeItems.get(1).test(inventory.getStack(1))) ||
                (recipeItems.get(0).test(inventory.getStack(1)) && recipeItems.get(1).test(inventory.getStack(0)));
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        // fixes the "[Render thread/WARN] (Minecraft) Unknown recipe category:" warning from Mojang hardcoding
        return true;
    }


    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    public int getTime() {
        return this.time;
    }

    public static class Type implements RecipeType<GeneratorRecipes> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "generator_recipe";
    }

    public static class Serializer implements RecipeSerializer<GeneratorRecipes> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "generator_recipe";
        // this is the name given in the json file

        @Override
        public GeneratorRecipes read(Identifier id, JsonObject json) {
            try {
                ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

                JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
                DefaultedList<Ingredient> inputs = DefaultedList.ofSize(2, Ingredient.EMPTY);

                for (int i = 0; i < inputs.size(); i++) {
                    inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                }
                int time = JsonHelper.getInt(json, "time");
                GeneratorMod.LOGGER.info("RECIPE INFO: " + id + " " + output + " " + inputs + " " + time);
                return new GeneratorRecipes(id, output, inputs, time);
            }
            catch (Exception e) {
                // avoid using the recipe if it's invalid
                return new GeneratorRecipes(id, ItemStack.EMPTY, DefaultedList.ofSize(2, Ingredient.EMPTY), 0);
            }
        }

        @Override
        public GeneratorRecipes read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            int time = buf.readInt();
            return new GeneratorRecipes(id, output, inputs, time);
        }

        @Override
        public void write(PacketByteBuf buf, GeneratorRecipes recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
            buf.writeInt(recipe.time);
        }
    }
}