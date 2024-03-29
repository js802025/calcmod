package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BarterSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    public static final Map<String, Double> barter = Map.ofEntries(
            Map.entry("soul_speed_book", 91.8),
        Map.entry("soul_speed_boots", 57.375),
        Map.entry("splash_fire_potion", 57.375),
        Map.entry("fire_potion", 57.375),
        Map.entry("iron_nugget", 1.996),
        Map.entry("ender_pearl", 15.3),
        Map.entry("string", 3.825),
        Map.entry("nether_quartz", 2.7),
        Map.entry("obsidian", 11.475),
        Map.entry("crying_obsidian", 5.7375),
        Map.entry("fire_charge", 11.475),
        Map.entry("leather", 3.825),
        Map.entry("soul_sand", 2.295),
        Map.entry("nether_brick", 2.295),
        Map.entry("spectral_arrow", 1.275),
        Map.entry("gravel", 0.96),
        Map.entry("blackstone", 0.96));
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (String b : barter.keySet()) {
            if (builder.getRemaining().isEmpty() || b.startsWith(builder.getRemaining())) {
                builder.suggest(b);
            }
        }



        
    return builder.buildFuture();
    }
    
}
