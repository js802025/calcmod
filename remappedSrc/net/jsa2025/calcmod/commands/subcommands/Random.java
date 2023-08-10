package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;



import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Random {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("random")
        .then(ClientCommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("minmax").then(ClientCommandManager.argument("min", StringArgumentType.string()).then(ClientCommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            String[] message = Help.execute("random");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("random")
        .then(CommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("minmax").then(CommandManager.argument("min", StringArgumentType.string()).then(CommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))))
        .then(CommandManager.literal("help").executes(ctx -> {
            String[] message = Help.execute("random");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }


    public static String[] execute(PlayerEntity player, String... range) {
        if (range.length == 1) {
        double maxInt = CalcCommand.getParsedExpression(player.getBlockPos(), range[0]);
        String random = nf.format(ThreadLocalRandom.current().nextInt(0, (int) maxInt + 1));
        return new String[] { "Random number between 0 and " + range[0] + " is ", random };
        } else if (range.length == 2 ) {
            double max = CalcCommand.getParsedExpression(player.getBlockPos(), range[1]);
            double min = CalcCommand.getParsedExpression(player.getBlockPos(), range[0]);
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
