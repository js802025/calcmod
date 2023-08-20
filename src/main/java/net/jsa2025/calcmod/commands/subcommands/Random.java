package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

//import net.fabricmc.fabric.api.client.command.v1.ClientCommands;
//import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class Random {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("random")
        .then(Commands.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("minmax").then(Commands.argument("min", StringArgumentType.string()).then(Commands.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(),  StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("random");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }


    public static String[] execute(ServerPlayer player, String... range) {
        if (range.length == 1) {
        double maxInt = CalcCommand.getParsedExpression(player.getOnPos(), range[0]);
        String random = nf.format(ThreadLocalRandom.current().nextInt(0, (int) maxInt + 1));
        return new String[] { "Random number between 0 and " + range[0] + " is ", random };
        } else if (range.length == 2 ) {
            double max = CalcCommand.getParsedExpression(player.getOnPos(), range[1]);
            double min = CalcCommand.getParsedExpression(player.getOnPos(), range[0]);
            String random = nf.format(ThreadLocalRandom.current().nextInt((int) min, (int) max + 1));
            return new String[] { "Random number between "+range[0]+" and " + range[1] + " is ", random };

        }
        return new String[] { "Invalid arguments" };
    }

    public static String helpMessage = """
        §LRandom:§r
            Given a min & max value, returns a random number between 0 and the max value.
            §cUsage: /calc random <max>§f
            """;
    
}
