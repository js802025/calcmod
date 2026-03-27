package net.jsa2025.calcmod.commands.arguments;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.commands.CommandSourceStack;

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
        Collection<RecipeHolder<?>> recipeStream = context.getSource().getLevel().recipeAccess().getRecipes();
        recipeStream.forEach(recipe -> {
            if (!recipe.value().display().isEmpty()) {
                String item = BuiltInRegistries.ITEM.getKey(recipe.value().display().get(0).result().resolveForFirstStack(SlotDisplayContext.fromLevel(context.getSource().getPlayer().level())).getItem()).getPath();
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
