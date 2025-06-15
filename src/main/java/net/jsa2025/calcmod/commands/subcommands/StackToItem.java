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
        stackToItemLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        stackToItemLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        stackToItemLiteral.then(ClientCommandManager.literal("convert")
            .then(ClientCommandManager.argument("numberofstacks", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
        
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> stackToItemLiteral = ClientCommandManager.literal("stacktoitem");
        populateClient(stackToItemLiteral);
        return stackToItemLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> stackToItemLiteral) {
        stackToItemLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        stackToItemLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        stackToItemLiteral.then(CommandManager.literal("convert")
            .then(CommandManager.argument("numberofstacks", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
        
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> stackToItemLiteral = CommandManager.literal("stacktoitem");
        populateServer(stackToItemLiteral);
        return stackToItemLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofstacks, int stackSize) {
        double stacks = CalcCommand.getParsedExpression(player, numberofstacks, 1); 
        if (stacks < 0) {
            return new CalcMessageBuilder().addString("Error: Number of stacks must be non-negative.");
        }
        double items = stacks * stackSize;
        return new CalcMessageBuilder().addInput(numberofstacks).addString(" Stacks (size "+stackSize+") = ").addResult(nf.format(items)).addString(" Items");
    }

    public static String helpMessage = """
        §b§LStack to Item:§r§f
        Converts a given number of stacks to the equivalent number of items. Assumes default stack size of 64 unless specified otherwise internally.
        Base command §e/calc stacktoitem§r or §e/calc stacktoitem help§r shows this message.
        
        §eUsage: /calc stacktoitem convert <numberofstacks>§f
          <numberofstacks>: Number of stacks (can be an expression, must be non-negative).
          Example: /calc stacktoitem convert 2.5
                """;
    
}
