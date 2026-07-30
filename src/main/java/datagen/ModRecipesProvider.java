package datagen;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.item.ModItems;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import javax.swing.text.html.HTML;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static final List<ItemLike> MYSTERIOUS_IRON =List.of(ModItems.MYSTERIOUS_IRON_ORE, ModBlocks.MYSTERIOUS_IRONSTONE);


    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        oreSmelting(recipeOutput, MYSTERIOUS_IRON, RecipeCategory.MISC, ModItems.MYSTERIOUS_IRON, 0.25f, 600, "mysterious_iron");
        oreBlasting(recipeOutput, MYSTERIOUS_IRON, RecipeCategory.MISC, ModItems.MYSTERIOUS_IRON, 0.25f, 200, "mysterious_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MYSTERIOUS_IRON_BLOCK)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#',ModItems.MYSTERIOUS_IRON)
                .unlockedBy(getHasName(ModItems.MYSTERIOUS_IRON), has(ModItems.MYSTERIOUS_IRON))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MERCURY_XIGUA_FRUIT_ITEM)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#',ModItems.MERCURY_XIGUA)
                .unlockedBy(getHasName(ModItems.MERCURY_XIGUA), has(ModItems.MERCURY_XIGUA))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.MYSTERIOUS_IRON,9)
                .requires(ModBlocks.MYSTERIOUS_IRON_BLOCK)
                .unlockedBy(getHasName(ModBlocks.MYSTERIOUS_IRON_BLOCK), has(ModBlocks.MYSTERIOUS_IRON_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.MERCURY_XIGUA_SEEDS,3)
                .requires(ModItems.MERCURY_XIGUA)
                .unlockedBy(getHasName(ModItems.MERCURY_XIGUA), has(ModItems.MERCURY_XIGUA))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.MERCURY_XIGUA,9)
                .requires(ModBlocks.MERCURY_XIGUA_FRUIT_ITEM)
                .unlockedBy(getHasName(ModBlocks.MERCURY_XIGUA_FRUIT_ITEM), has(ModBlocks.MERCURY_XIGUA_FRUIT_ITEM))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.STICK_IN_LEATHER)
                .pattern("x")
                .pattern("#")
                .define('x', Items.STICK)
                .define('#', Items.LEATHER)
                .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER))
                .save(recipeOutput, xiguaworld.MOD_ID + ":stick_in_leather");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MYSTERIOUS_IRON_SWORD)
                .pattern(" x ")
                .pattern("yxy")
                .pattern(" # ")
                .define('x', ModItems.MYSTERIOUS_IRON)
                .define('y', Items.IRON_INGOT)
                .define('#',ModItems.STICK_IN_LEATHER)
                .unlockedBy(getHasName(ModItems.MYSTERIOUS_IRON), has(ModItems.MYSTERIOUS_IRON))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.WOOL_PAD)
                .pattern(" x")
                .pattern("x ")
                .define('x', Items.WHITE_WOOL)
                .unlockedBy(getHasName(Items.WHITE_WOOL), has(Items.WHITE_WOOL))
                .save(recipeOutput, xiguaworld.MOD_ID + ":wool_pad");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MYSTERIOUS_IRON_HELMET)
                .pattern("xxx")
                .pattern("xyx")
                .define('x', ModItems.MYSTERIOUS_IRON)
                .define('y', ModItems.WOOL_PAD)
                .unlockedBy(getHasName(ModItems.MYSTERIOUS_IRON), has(ModItems.MYSTERIOUS_IRON))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MYSTERIOUS_IRON_CHESTPLATE)
                .pattern("xyx")
                .pattern("xxx")
                .pattern("xxx")
                .define('x', ModItems.MYSTERIOUS_IRON)
                .define('y', ModItems.WOOL_PAD)
                .unlockedBy(getHasName(ModItems.MYSTERIOUS_IRON), has(ModItems.MYSTERIOUS_IRON))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MYSTERIOUS_IRON_LEGGINGS)
                .pattern("xxx")
                .pattern("x x")
                .pattern("xyx")
                .define('x', ModItems.MYSTERIOUS_IRON)
                .define('y', ModItems.WOOL_PAD)
                .unlockedBy(getHasName(ModItems.MYSTERIOUS_IRON), has(ModItems.MYSTERIOUS_IRON))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MYSTERIOUS_IRON_BOOTS)
                .pattern("xyx")
                .pattern("x x")
                .define('x', ModItems.MYSTERIOUS_IRON)
                .define('y', ModItems.WOOL_PAD)
                .unlockedBy(getHasName(ModItems.MYSTERIOUS_IRON), has(ModItems.MYSTERIOUS_IRON))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,ModItems.MEMORY_PRISM)
                .pattern("xxx")
                .pattern("xyx")
                .pattern("xxx")
                .define('x', Items.GLASS_PANE)
                .define('y', Items.GLOWSTONE_DUST)
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .save(recipeOutput);
    }


    protected static void oreSmelting(
            RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group
    ) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.SMELTING_RECIPE,
                SmeltingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_smelting"
        );
    }

    protected static void oreBlasting(
            RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group
    ) {
        oreCooking(
                recipeOutput,
                RecipeSerializer.BLASTING_RECIPE,
                BlastingRecipe::new,
                ingredients,
                category,
                result,
                experience,
                cookingTime,
                group,
                "_from_blasting"
        );
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(
            RecipeOutput recipeOutput,
            RecipeSerializer<T> serializer,
            AbstractCookingRecipe.Factory<T> recipeFactory,
            List<ItemLike> ingredients,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int cookingTime,
            String group,
            String suffix
    ) {
        for (ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, serializer, recipeFactory)
                    .group(group)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, xiguaworld.MOD_ID+":"+getItemName(result) + suffix + "_" + getItemName(itemlike));
        }
    }
}
