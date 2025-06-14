package net.jsa2025.calcmod.commands.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

// import net.minecraft.recipe.CraftingRecipe; // No longer needed
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
// import net.minecraft.registry.Registries; // No longer needed
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
// import net.jsa2025.calcmod.CalcMod; // No longer needed for logger

import java.util.Optional;

public class RecipeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    // Constants for serializer IDs are no longer needed with direct comparison

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        RecipeManager recipeManager = context.getSource().getServer().getRecipeManager(); // Use getServer() for ServerCommandSource
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
                // Logging for filtered recipes removed as per requirement
            }
        });
        
        return builder.buildFuture();
    }
    
}
