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

public class SecondsToHopperClock {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("secondstohopperclock").then(ClientCommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            String[] message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("secondstohopperclock").then(CommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("help").executes(ctx -> {
            String[] message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static String[] execute(PlayerEntity player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player.getBlockPos(), seconds);
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
