package net.jsa2025.calcmod.commands.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.CalcMod;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

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
        Stream<RecipeCollection> recipeStream = context.getSource().getPlayer().getRecipeBook().getCollections().stream();
        recipeStream.forEach(recipe -> {
            String item = getFullPath(BuiltInRegistries.ITEM.getKey(recipe.getRecipes().get(0).display().result().resolveForStacks(SlotDisplayContext.fromLevel(context.getSource().getPlayer().level())).get(0).getItem()));
            if (item == null) {
                return;
            }
            if (builder.getRemaining().isEmpty() || item.startsWith(builder.getRemaining()) || item.split(":")[1].startsWith(builder.getRemaining())) {
                builder.suggest(item);
            }
        });



        
    return builder.buildFuture();
    }

    public static String getFullPath(Identifier id) {
        return id.getNamespace() + ":" + id.getPath();
    }
    
}
