package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.jsa2025.calcmod.commands.subcommands.Custom;
import net.minecraft.command.CommandSource;


import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CustomFunctionProvider implements SuggestionProvider<CommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        ArrayList<String> parsedFuncs = Custom.getParsedFunctions();
        parsedFuncs.forEach((String func) -> {
            builder.suggest(func.split("= ")[0]);
        });


        return builder.buildFuture();
    }
    }
