package net.jsa2025.calcmod.commands.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.util.Identifier;

public class CRecipeSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
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
        Stream<RecipeResultCollection> recipeStream = context.getSource().getPlayer().getRecipeBook().getOrderedResults().stream();
        recipeStream.forEach(recipe -> {
            String item = recipe.getAllRecipes().get(0).display().result().getStacks(SlotDisplayContexts.createParameters(context.getSource().getPlayer().getWorld())).get(0).getRegistryEntry().getIdAsString();
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
