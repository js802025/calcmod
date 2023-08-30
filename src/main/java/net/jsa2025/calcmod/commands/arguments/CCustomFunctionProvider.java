package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.subcommands.Custom;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CCustomFunctionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        ArrayList<String> parsedFuncs = Custom.getParsedFunctions();
        parsedFuncs.forEach((String func) -> {
            builder.suggest(func.split("= ")[0]);
        });


        return builder.buildFuture();
    }
    }
