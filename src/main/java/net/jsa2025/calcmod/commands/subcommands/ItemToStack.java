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

public class ItemToStack {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> itemToStackLiteral) {
        itemToStackLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        itemToStackLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        itemToStackLiteral.then(ClientCommandManager.literal("convert")
            .then(ClientCommandManager.argument("numberofitems", StringArgumentType.string())
            .executes(ctx -> {
                String numberofitemsArg = StringArgumentType.getString(ctx, "numberofitems");
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), numberofitemsArg, 64);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> itemToStackLiteral = ClientCommandManager.literal("itemtostack");
        populateClient(itemToStackLiteral);
        return itemToStackLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> itemToStackLiteral) {
        itemToStackLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });
        
        itemToStackLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        itemToStackLiteral.then(CommandManager.literal("convert")
            .then(CommandManager.argument("numberofitems", StringArgumentType.string())
            .executes(ctx -> {
                String numberofitemsArg = StringArgumentType.getString(ctx, "numberofitems");
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), numberofitemsArg, 64);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> itemToStackLiteral = CommandManager.literal("itemtostack");
        populateServer(itemToStackLiteral);
        return itemToStackLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player, numberofitems, stackSize);
        if (items < 0) {
            return new CalcMessageBuilder().addString("Error: Number of items must be non-negative.");
        }
        double stacks = Math.floor(items/stackSize);
        double leftover = items % stackSize;
        CalcMessageBuilder message = new CalcMessageBuilder().addInput(numberofitems).addString(" Items (stack size "+stackSize+") = ").addResult(df.format(stacks)).addString(" Stacks + ").addResult(df.format(leftover)).addString(" Items");
        
        return message;
    }

    public static String helpMessage = """
        §b§LItem to Stack:§r§f
        Converts a given number of items to stacks and remainder items. Assumes default stack size of 64 unless specified otherwise internally.
        Base command §e/calc itemtostack§r or §e/calc itemtostack help§r shows this message.
        
        §eUsage: /calc itemtostack convert <numberofitems>§f
          <numberofitems>: Number of items (can be an expression, must be non-negative).
          Example: /calc itemtostack convert 129
                """;
}
