package net.jsa2025.calcmod.commands.arguments;


import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer; 

import java.util.Optional;

public class CRecipeSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        RecipeManager recipeManager = context.getSource().getWorld().getRecipeManager();
        String remaining = builder.getRemaining().toLowerCase();

        recipeManager.keys().forEach(recipeId -> {
            Optional<RecipeEntry<?>> recipeEntryOptional = recipeManager.get(recipeId);
            if (recipeEntryOptional.isPresent()) {
                Recipe<?> recipe = recipeEntryOptional.get().value();
                RecipeSerializer<?> serializer = recipe.getSerializer();

                if (serializer == RecipeSerializer.SHAPED || serializer == RecipeSerializer.SHAPELESS) {
                    String idString = recipeId.toString();
                    if (idString.toLowerCase().contains(remaining)) {
                        builder.suggest(idString);
                    }
                }
            }
        });
        return builder.buildFuture();
    }
    
}
