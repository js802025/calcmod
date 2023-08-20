package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class SecondsToHopperClock {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("secondstohopperclock").then(Commands.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayer player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player.getOnPos(), seconds);
        double hopperclock = Math.ceil(secondsDouble *1.25);
        if (hopperclock > 320) {
            String[] message = {"Hopper Clock Total Items: ", nf.format(hopperclock), "", " \nStacks: "+nf.format(Math.floor(hopperclock/64))+" Items: "+nf.format(hopperclock%64), " \n§cThis exceeds the maximum items of a hopper."};
            return message;
        } else {
        String[] message = {"Hopper Clock Total Items: ", nf.format(hopperclock), "", " \nStacks: "+nf.format(Math.floor(hopperclock/64))+" Items: "+nf.format(hopperclock%64)};
        return message;
        }
    }

    public static String helpMessage = """
        §LSeconds to Hopper Clock:§r
            Given a number of seconds (can be in expression form), returns the number of ticks in a hopper clock
            §cUsage: /calc secondstohopperclock <seconds>§f
                """;

}
