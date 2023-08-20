package net.jsa2025.calcmod.commands.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;

public class RecipeSuggestionProvider implements SuggestionProvider<CommandSource> {
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
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
        Stream<ResourceLocation> recipeStream = context.getSource().getServer().getRecipeManager().getRecipeIds();
        recipeStream.forEach(recipe -> {
            String item = recipe.getPath();
            if (item == null) {
                return;
            }
            if (builder.getRemaining().isEmpty() || item.startsWith(builder.getRemaining())) {
                builder.suggest(item);
            }
        });



        
    return builder.buildFuture();
    }
    
}
