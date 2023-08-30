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

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Random {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(CommandManager.literal("random")
        .then(CommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(CommandManager.literal("minmax").then(CommandManager.argument("min", StringArgumentType.string()).then(CommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(),  StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))))
        .then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("random");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }


    public static CalcMessageBuilder execute(Entity player, String... range) {
        if (range.length == 1) {
        double maxInt = CalcCommand.getParsedExpression(player, range[0]);
        String random = nf.format(ThreadLocalRandom.current().nextInt(0, (int) maxInt + 1));
        return new CalcMessageBuilder().addFromArray(new String[] { "Random number between 0 and ", "input", " §7(inclusive)§f = ", "result" }, range, new String[] {random});
        } else if (range.length == 2 ) {
            double max = CalcCommand.getParsedExpression(player, range[1]);
            double min = CalcCommand.getParsedExpression(player, range[0]);
            String random = nf.format(ThreadLocalRandom.current().nextInt((int) min, (int) max + 1));
            return new CalcMessageBuilder().addFromArray(new String[] { "Random number between ", "input", " and ", "input", " §7(inclusive)§f = ", "result" }, range, new String[] {random});

        }
        return new CalcMessageBuilder("Invalid Arguments");
    }

    public static String helpMessage = """
        §b§LRandom:§r§f
            Given a maximum and/or minimum value, returns a random number between those values §7(inclusive)§r. If just a maximum value is entered, picks a random number from 0 to the max value §7(inclusive)§r.
            §eUsage: /calc random <max>§f
            """;
    
}
