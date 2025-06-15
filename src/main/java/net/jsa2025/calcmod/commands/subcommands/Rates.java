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

public class Rates {
    static DecimalFormat df = new DecimalFormat("#.##"); 
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> ratesLiteral) {
        ratesLiteral.executes(ctx ->{ 
            CalcMessageBuilder message = Help.executeSpecificHelp("rates");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
        
        ratesLiteral.then(ClientCommandManager.literal("help").executes(ctx ->{
            CalcMessageBuilder message = Help.executeSpecificHelp("rates");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        
        ratesLiteral.then(ClientCommandManager.literal("calculate")
            .then(ClientCommandManager.argument("numberofitems", StringArgumentType.string())
            .then(ClientCommandManager.argument("time", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            }))));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> ratesLiteral = ClientCommandManager.literal("rates");
        populateClient(ratesLiteral);
        return ratesLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> ratesLiteral) {
        ratesLiteral.executes(ctx ->{ 
            CalcMessageBuilder message = Help.executeSpecificHelp("rates");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        ratesLiteral.then(CommandManager.literal("help").executes(ctx ->{
            CalcMessageBuilder message = Help.executeSpecificHelp("rates");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        ratesLiteral.then(CommandManager.literal("calculate")
            .then(CommandManager.argument("numberofitems", StringArgumentType.string())
            .then(CommandManager.argument("time", StringArgumentType.string()) 
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            }))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> ratesLiteral = CommandManager.literal("rates");
        populateServer(ratesLiteral);
        return ratesLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofitems, String time) {
        double items = CalcCommand.getParsedExpression(player, numberofitems);
        double timeDouble = CalcCommand.getParsedExpression(player, time);

        if (timeDouble == 0) {
            return new CalcMessageBuilder().addString("Error: Time cannot be zero.");
        }
        if (items < 0 || timeDouble < 0) {
            return new CalcMessageBuilder().addString("Error: Number of items and time must be non-negative.");
        }

        double itemspersecond = items / timeDouble;
        double calculatedRate = itemspersecond * 3600;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " Items in ", "input", " Seconds = ", "result", "/hr"}, new String[] {numberofitems, time}, new String[] {df.format(calculatedRate)});
        return message;
    }

    public static String helpMessage = """
        §b§LRates:§r§f
        Calculates items per hour given a number of items and time in seconds.
        Base command §e/calc rates§r or §e/calc rates help§r shows this message.
        
        §eUsage: /calc rates calculate <numberofitems> <time_in_seconds>§f
          <numberofitems>: Number of items (can be an expression, non-negative).
          <time_in_seconds>: Duration in seconds (can be an expression, non-negative, not zero).
          Example: /calc rates calculate 1500 30
                """;
}
