package net.jsa2025.calcmod.commands.arguments;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class RecipeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        List<RecipeHolder<CraftingRecipe>> recipes = context.getSource().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        for (RecipeHolder<CraftingRecipe> recipe : recipes) {
            if (recipe.value() instanceof ShapedRecipe || recipe.value() instanceof ShapelessRecipe) {
                String id = recipe.id().toString();
                if (builder.getRemaining().isEmpty() || id.startsWith(builder.getRemaining())) {
                    builder.suggest(id);
                }
            }
        }
        return builder.buildFuture();
    }
}
