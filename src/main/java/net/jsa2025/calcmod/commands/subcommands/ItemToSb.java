package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ItemToSb {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> itemToSbLiteral) {
        // Defines "itemtosb <numberofitems>" (defaulting to stackSize 64)
        // and "itemtosb help"
        // Path 1: /calc itemtosb help (Literal, most specific)
        itemToSbLiteral.then(ClientCommandManager.literal("help").executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        // Path 2: /calc itemtosb <numberofitems> (Greedy string, more general)
        itemToSbLiteral.then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            }));
        
        // Base command /calc itemtosb shows help
        itemToSbLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> itemToSbLiteral = ClientCommandManager.literal("itemtosb");
        populateClient(itemToSbLiteral);
        return itemToSbLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> itemToSbLiteral) {
        // Path 1: /calc itemtosb help (Literal, most specific)
        itemToSbLiteral.then(CommandManager.literal("help").executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        // Path 2: /calc itemtosb <numberofitems> (Greedy string, more general)
        itemToSbLiteral.then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            }));
        
        // Base command /calc itemtosb shows help
        itemToSbLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> itemToSbLiteral = CommandManager.literal("itemtosb");
        populateServer(itemToSbLiteral);
        return itemToSbLiteral;
    }

    // Execute method remains the same, stackSize is passed by the command's executes block
    public static CalcMessageBuilder execute(Entity player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player, numberofitems, stackSize);
        double sbs = items / (stackSize * 27);
        CalcMessageBuilder message= new CalcMessageBuilder().addInput(numberofitems).addString(" Items = ").addResult(nf.format(sbs)).addString(" SBs");

        return message;
    }

    public static String helpMessage = """
        §b§LItem to Sb:§r§f
            Given a number of items §7§o(can be in expression form)§r§f, returns the number of Shulker Boxes.
            §eUsage: /calc itemtosb <numberofitems>§f
                """;
}
