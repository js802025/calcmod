package net.jsa2025.calcmod.commands.arguments;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;

import net.jsa2025.calcmod.CalcMod;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.Recipe;

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
        Iterator<Recipe> recipeStream = context.getSource().getExecutor().getServer().recipeIterator();
      //  CalcMod.LOGGER.info("RECIPES: "+recipeStream.hasNext());

        while (recipeStream.hasNext()) {
            Recipe recipe = recipeStream.next();
          //  CalcMod.LOGGER.info(recipe.getClass().getName());
           if (recipe.getClass().getName().contains("Shape")) {
             //   CalcMod.LOGGER.info("RECIPE:"+String.valueOf(((CraftingRecipe) recipe).getKey()));
                builder.suggest(String.valueOf(((CraftingRecipe) recipe).getKey()));
            }
        }



        
    return builder.buildFuture();
    }
    
}
