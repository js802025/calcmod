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

public class StackToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> stackToItemLiteral) {
        // Path 1: /calc stacktoitem help (Literal, most specific)
        stackToItemLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        // Path 2: /calc stacktoitem <numberofstacks> (Greedy string, more general)
        stackToItemLiteral.then(ClientCommandManager.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        
        // Base command shows help
        stackToItemLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> stackToItemLiteral = ClientCommandManager.literal("stacktoitem");
        populateClient(stackToItemLiteral);
        return stackToItemLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> stackToItemLiteral) {
        // Path 1: /calc stacktoitem help (Literal, most specific)
        stackToItemLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        // Path 2: /calc stacktoitem <numberofstacks> (Greedy string, more general)
        stackToItemLiteral.then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));
        
        // Base command shows help
        stackToItemLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> stackToItemLiteral = CommandManager.literal("stacktoitem");
        populateServer(stackToItemLiteral);
        return stackToItemLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofstacks, int stackSize) {
        double stacks = CalcCommand.getParsedExpression(player, numberofstacks, 1);
        double items = stacks * stackSize;
        return new CalcMessageBuilder().addInput(numberofstacks).addString(" ").addInput(nf.format(stackSize)).addString(" Stacks = ").addResult(nf.format(items)).addString(" Items");
    }

    public static String helpMessage = """
        §b§LStack to Item:§r§f
            Given a number of stacks §7§o(can be in expression form)§r§f, returns the number of items.
            §eUsage: /calc stacktoitem <numberofstacks>§f
                """;
    
}
