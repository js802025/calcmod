package net.jsa2025.calcmod.commands.arguments;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.server.command.ServerCommandSource;

public class RecipeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
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
        Collection<RecipeEntry<?>> recipeStream = context.getSource().getWorld().getRecipeManager().values();
        recipeStream.forEach(recipe -> {
            if (!recipe.value().getDisplays().isEmpty()) {
                String item = recipe.value().getDisplays().get(0).result().getFirst(SlotDisplayContexts.createParameters(context.getSource().getPlayer().getEntityWorld())).getRegistryEntry().getIdAsString();
                if (item == null) {
                    return;
                }
                if (builder.getRemaining().isEmpty() || item.startsWith(builder.getRemaining()) || item.split(":")[1].startsWith(builder.getRemaining())) {
                    builder.suggest( item);
                }
            }
        });



        
    return builder.buildFuture();
    }
    
}
