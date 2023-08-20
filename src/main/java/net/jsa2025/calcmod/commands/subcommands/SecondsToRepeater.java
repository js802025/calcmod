package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class SecondsToRepeater {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("secondstorepeater").then(Commands.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("secondstorepeater");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayer player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player.getOnPos(), seconds);
        double ticks = secondsDouble * 10;
        double repeaters = Math.ceil(ticks/4);
        if (ticks % 4 != 0) {
            String[] message = {"Repeaters Required: ", nf.format(repeaters), " \nLast Repeater Tick: ", nf.format(ticks % 4)} ;
            return message;
        } else {
            String[] message = {"Repeaters Required: ", nf.format(Math.ceil(ticks/4))};
            return message;
        }
    }

    public static String helpMessage = """
        §LSeconds to Repeater:§r
            Given a number of seconds (can be in expression form), returns the number of repeaters and the last tick of the last repeater
            §cUsage: /calc secondstorepeater <seconds>§f
                """;
}
