package net.jsa2025.calcmod.commands.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

public class RecipeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        // context.getSource().getWorld().getRecipeManager().keys().map(recipe -> {
        //     String item = recipe.getNamespace();
        //     if (item == null) {
        //         return item;
        //     }
        //     if (builder.getRemaining().isEmpty() || item.startsWith(builder.getRemaining())) {
        //         builder.suggest(item);
        //     }

        //     return item;
        // });
        Stream<ResourceLocation> recipeStream = context.getSource().getRecipeManager().getRecipeIds();
        recipeStream.forEach(recipe -> {
            String item = recipe.getPath();
            if (item == null) {
                return;
            }
            if (builder.getRemaining().isEmpty() || item.startsWith(builder.getRemaining())) {
                builder.suggest(recipe.getNamespace()+":"+item);
            }
        });



        
    return builder.buildFuture();
    }
    
}
