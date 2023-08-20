package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

public class Rates {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("rates").then(Commands.argument("numberofitems", StringArgumentType.string())
        .then(Commands.argument("time", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx ->{
            String[] message = Help.execute("rates");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));

        return command;
    }

    public static String[] execute(ServerPlayerEntity player, String numberofitems, String time) {
        double items = CalcCommand.getParsedExpression(player.getEntity().blockPosition(), numberofitems);
        double timeDouble = CalcCommand.getParsedExpression(player.getEntity().blockPosition(), time);
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
