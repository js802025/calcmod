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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;

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
        Stream<RecipeHolder<?>> recipeStream = context.getSource().getRecipeManager().getRecipes().stream();
        recipeStream.forEach(recipe -> {
            if (!recipe.value().display().isEmpty() && (recipe.value().display().get(0).type().equals(ShapedCraftingRecipeDisplay.TYPE) || recipe.value().display().get(0).type().equals(ShapelessCraftingRecipeDisplay.TYPE)))
            {
                String item = recipe.id().registry().getPath();
                if (item == null) {
                    return;
                }
                if (builder.getRemaining().isEmpty() || item.startsWith(builder.getRemaining())) {
                    builder.suggest(recipe.id().registry() + ":" + item);
                }
            }
        });



        
    return builder.buildFuture();
    }
    
}
