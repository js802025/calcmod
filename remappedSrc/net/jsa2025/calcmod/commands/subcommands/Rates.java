package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Rates {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("rates").then(ClientCommandManager.argument("numberofitems", StringArgumentType.string())
        .then(ClientCommandManager.argument("time", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.literal("help").executes(ctx ->{
            String[] message = Help.execute("rates");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));

        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("rates").then(CommandManager.argument("numberofitems", StringArgumentType.string())
        .then(CommandManager.argument("time", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("help").executes(ctx ->{
            String[] message = Help.execute("rates");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));

        return command;
    }

    public static String[] execute(Entity player, String numberofitems, String time) {
        double items = CalcCommand.getParsedExpression(player, numberofitems);
        double timeDouble = CalcCommand.getParsedExpression(player, time);
        double itemspersecond = items / timeDouble;
        double rates = itemspersecond * 3600;
        String[] message = {"Rates: ", nf.format(rates)};
        return message;
    }

    public static String helpMessage = """
        §LRates:§r
            Given a number of items and afk time in seconds (can be in expression form), returns the number of items per hour
            §cUsage: /calc rates <numberofitems> <time>§f
                """;
}
