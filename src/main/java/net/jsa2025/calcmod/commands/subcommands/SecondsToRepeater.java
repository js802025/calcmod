package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.world.entity.Entity;


public class SecondsToRepeater {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("secondstorepeater").then(Commands.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstorepeater");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(Entity player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player, seconds);
        double ticks = secondsDouble * 10;
        double repeaters = Math.ceil(ticks/4);
        if (ticks % 4 != 0) {
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Repeaters required for ", "input", " seconds = ", "result", " \nLast repeater tick = ", "result"}, new String[] {seconds}, new String[] {nf.format(repeaters), nf.format(ticks % 4)});
            return message;
        } else {
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Repeaters required for ", "input", " seconds = ", "result"}, new String[] {seconds}, new String[] {nf.format(repeaters)});
            return message;
        }
    }

    public static String helpMessage = """
        §b§LSeconds to Repeater:§r§f
            Given a number of seconds §7§o(can be in expression form)§r§f, returns the number of repeaters and their delay.
            §eUsage: /calc secondstorepeater <seconds>§f
                """;
}
