package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

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
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> secondsToHopperClockLiteral) {
        secondsToHopperClockLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
        
        secondsToHopperClockLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        
        secondsToHopperClockLiteral.then(ClientCommandManager.literal("convert")
            .then(ClientCommandManager.argument("seconds", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> secondsToHopperClockLiteral = ClientCommandManager.literal("secondstohopperclock");
        populateClient(secondsToHopperClockLiteral);
        return secondsToHopperClockLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> secondsToHopperClockLiteral) {
        secondsToHopperClockLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        secondsToHopperClockLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstohopperclock");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        secondsToHopperClockLiteral.then(CommandManager.literal("convert")
            .then(CommandManager.argument("seconds", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> secondsToHopperClockLiteral = CommandManager.literal("secondstohopperclock");
        populateServer(secondsToHopperClockLiteral);
        return secondsToHopperClockLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player, seconds);
        if (secondsDouble < 0) {
            return new CalcMessageBuilder().addString("Error: Seconds must be a non-negative value.");
        }
        double hopperclock = Math.ceil(secondsDouble * 1.25);
        String stacksMessage = "";
        if (hopperclock > 0) { 
            stacksMessage = " \nStacks: "+nf.format(Math.floor(hopperclock/64))+" Items: "+nf.format(hopperclock%64);
        }

        if (hopperclock > 320) {
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Items needed in hopper clock for ", "input"," seconds = ", "result", "result", " \n§cThis exceeds the maximum number of items in a hopper."}, new String[] {seconds}, new String[] {nf.format(hopperclock), stacksMessage});
            return message;
        } else {
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Items needed in hopper clock for ", "input"," seconds = ", "result", "result"}, new String[] {seconds}, new String[] {nf.format(hopperclock), stacksMessage});
        return message;
        }
    }

    public static String helpMessage = """
        §b§LSeconds to Hopper Clock:§r§f
        Calculates items needed in a hopper clock for a given duration.
        Base command §e/calc secondstohopperclock§r or §e/calc secondstohopperclock help§r shows this message.
        
        §eUsage: /calc secondstohopperclock convert <seconds>§f
          <seconds>: Duration in seconds (can be an expression, must be non-negative).
          Example: /calc secondstohopperclock convert 100
                """;

}
