package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SecondsToHopperClock {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("secondstohopperclock").then(ClientCommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("secondstohopperclock").then(CommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(Entity player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player, seconds);
        double hopperclock = Math.ceil(secondsDouble *1.25);
        if (hopperclock > 320) {
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Items needed in hopper clock for ", "input"," seconds = ", "result", "result", " \n§cThis exceeds the maximum number of items in a hopper."}, new String[] {seconds}, new String[] {nf.format(hopperclock), " \nStacks: "+nf.format(Math.floor(hopperclock/64))+" Items: "+nf.format(hopperclock%64)});
            return message;
        } else {
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Items needed in hopper clock for ", "input"," seconds = ", "result", "result"}, new String[] {seconds}, new String[] {nf.format(hopperclock), " \nStacks: "+nf.format(Math.floor(hopperclock/64))+" Items: "+nf.format(hopperclock%64)});
        return message;
        }
    }

    public static String helpMessage = """
        §b§LSeconds to Hopper Clock:§r§f
            Given a number of seconds §7§o(can be in expression form)§r§f, returns the number of items needed in a hopper clock to achieve that time.
            §eUsage: /calc secondstohopperclock <seconds>§f
                """;

}
