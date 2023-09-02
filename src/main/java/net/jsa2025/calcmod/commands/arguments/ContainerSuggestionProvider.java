package net.jsa2025.calcmod.commands.arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;

public class ContainerSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    public static Map<String, Integer> containers;

    static {
        containers = new HashMap<>();
        containers.put("shulker_box", 27);
        containers.put("chest", 27);
        containers.put("barrel", 27);
        containers.put("trapped_chest", 27);
        containers.put("double_chest", 54);
        containers.put("dropper", 9);
        containers.put("dispenser", 9);
        containers.put("hopper",5);
        containers.put("hopper_minecart", 5);
        containers.put("brewing_stand", 5);
        containers.put("furnace", 3);
        containers.put("blast_furnace", 3);
        containers.put("smoker", 3);
    }
    
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
