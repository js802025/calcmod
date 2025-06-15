package net.jsa2025.calcmod.commands.subcommands;


import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.CContainerSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.ContainerSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SignalToItems {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> signalToItemsLiteral) {
        signalToItemsLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("signaltoitems");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
        
        signalToItemsLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("signaltoitems");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        
        signalToItemsLiteral.then(ClientCommandManager.literal("get")
            .then(ClientCommandManager.argument("container_type", StringArgumentType.string()).suggests(new CContainerSuggestionProvider())
                .then(ClientCommandManager.argument("signal_strength", StringArgumentType.string())
                .executes((ctx) -> {
                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "container_type"), StringArgumentType.getString(ctx, "signal_strength"));
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }))));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> signalToItemsLiteral = ClientCommandManager.literal("signaltoitems");
        populateClient(signalToItemsLiteral);
        return signalToItemsLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> signalToItemsLiteral) {
        signalToItemsLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("signaltoitems");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        signalToItemsLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("signaltoitems");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        signalToItemsLiteral.then(CommandManager.literal("get")
            .then(CommandManager.argument("container_type", StringArgumentType.string()).suggests(new ContainerSuggestionProvider())
                .then(CommandManager.argument("signal_strength", StringArgumentType.string())
                .executes((ctx) -> {
                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "container_type"), StringArgumentType.getString(ctx, "signal_strength"));
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> signalToItemsLiteral = CommandManager.literal("signaltoitems");
        populateServer(signalToItemsLiteral);
        return signalToItemsLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String container, String signal) {
        double strength = CalcCommand.getParsedExpression(player, signal);
        
        if (strength < 0 || strength > 15) { 
            return new CalcMessageBuilder().addString("Error: Signal Strength must be between 0 and 15 (inclusive).");
        }

        var containers = ContainerSuggestionProvider.containers;
        if (!containers.containsKey(container)) {
            return new CalcMessageBuilder().addString("Error: Container type '"+container+"' not recognized. Please use one of the suggested container types.");
        }
        double containerSlots = containers.get(container); 
        String items64Str, items16Str, items1Str;

        if (strength == 0) {
            items64Str = "0";
            items16Str = "0";
            items1Str = "0";
        } else {
            // For stackable to 64
            double items64 = Math.ceil( ( (strength - 1 + (1.0/256.0)) / 14.0 ) * 64.0 * containerSlots );
            items64 = Math.max(1, items64); 
            if (strength == 15) items64 = 64.0 * containerSlots; // Full
            items64Str = CalcCommand.getParsedStack((long)items64, 64);

            // For stackable to 16
            double items16 = Math.ceil( ( (strength - 1 + (1.0/256.0)) / 14.0 ) * 16.0 * containerSlots );
            items16 = Math.max(1, items16);
            if (strength == 15) items16 = 16.0 * containerSlots;
            items16Str = CalcCommand.getParsedStack((long)items16, 16);
            
            // For non-stackable (stack size 1)
            double items1 = Math.ceil( ( (strength - 1 + (1.0/256.0)) / 14.0 ) * 1.0 * containerSlots );
            items1 = Math.max(1, items1);
            if (strength == 15) items1 = 1.0 * containerSlots;
            items1Str = nf.format((long)items1);
        }
        
        CalcMessageBuilder message = new CalcMessageBuilder()
            .addString("For signal ").addInput(signal).addString(" with ").addInput(container).addString(":\n")
            .addString("  Stackable (64): ").addResult(items64Str).addString("\n")
            .addString("  Stackable (16): ").addResult(items16Str).addString("\n")
            .addString("  Non-stackable (1): ").addResult(items1Str);
        
        return message;
    }

    public static String helpMessage = """
        §b§LSignal To Items:§r§f
        Calculates items needed in a container for a specific comparator signal strength.
        Base command §e/calc signaltoitems§r or §e/calc signaltoitems help§r shows this message.
        
        §eUsage: /calc signaltoitems get <container_type> <signal_strength>§f
          <container_type>: Type of container (e.g., chest, hopper, furnace).
          <signal_strength>: Desired signal strength (0-15).
          Example: /calc signaltoitems get chest 7
                """;

}
