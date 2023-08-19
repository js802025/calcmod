package net.jsa2025.calcmod.commands.arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
public class ContainerSuggestionProvider implements SuggestionProvider {
    public static final Map<String, Integer> containers = Map.ofEntries(Map.entry("shulker_box", 27),
    Map.entry("chest", 27),
    Map.entry("barrel", 27),
    Map.entry("trapped_chest", 27),
    Map.entry("double_chest", 54),
    Map.entry("dropper", 9),
    Map.entry("dispenser", 9),
    Map.entry("hopper",5),
    Map.entry("hopper_minecart", 5),
    Map.entry("brewing_stand", 5),
    Map.entry("furnace", 3),
    Map.entry("blast_furnace", 3),
    Map.entry("smoker", 3)
    );
    
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) {
        // Map<String, Integer> containers = new HashMap<String, Integer>();
        // containers.put("shulker_box", 27);
        // containers.put("chest", 27);
        // containers.put("barrel", 27);
        // containers.put("trapped_chest", 27);
        // containers.put("double_chest", 54);
        // containers.put("dropper", 9);
        // containers.put("dispenser", 9);
        // containers.put("furnace", 3);
        // containers.put("blast_furnace", 3);
        // containers.put("smoker", 3);
        // containers.put("hopper", 5);
        // containers.put("brewing_stand", 5);
        // containers.put("hopper_minecart", 5);
        for (String container : containers.keySet()) {
            if (builder.getRemaining().isEmpty() || container.startsWith(builder.getRemaining())) {
                builder.suggest(container);
            }
        }



        
    return builder.buildFuture();
    }
    
}
