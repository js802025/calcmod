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

public class SecondsToRepeater {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> secondsToRepeaterLiteral) {
        secondsToRepeaterLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("secondstorepeater");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
        
        secondsToRepeaterLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstorepeater");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        secondsToRepeaterLiteral.then(ClientCommandManager.literal("convert")
            .then(ClientCommandManager.argument("seconds", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> secondsToRepeaterLiteral = ClientCommandManager.literal("secondstorepeater");
        populateClient(secondsToRepeaterLiteral);
        return secondsToRepeaterLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> secondsToRepeaterLiteral) {
        secondsToRepeaterLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstorepeater");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        secondsToRepeaterLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("secondstorepeater");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        secondsToRepeaterLiteral.then(CommandManager.literal("convert")
            .then(CommandManager.argument("seconds", StringArgumentType.string())
            .executes(ctx -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "seconds"));
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> secondsToRepeaterLiteral = CommandManager.literal("secondstorepeater");
        populateServer(secondsToRepeaterLiteral);
        return secondsToRepeaterLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String seconds) {
        double secondsDouble = CalcCommand.getParsedExpression(player, seconds);
        if (secondsDouble < 0) {
            return new CalcMessageBuilder().addString("Error: Seconds must be a non-negative value.");
        }
        double ticks = secondsDouble * 10;
        if (ticks == 0) { 
             return new CalcMessageBuilder().addFromArray(new String[] {"Repeaters required for ", "input", " seconds = ", "result"}, new String[] {seconds}, new String[] {"0"});
        }
        double repeaters = Math.ceil(ticks/4);
        double lastRepeaterDelay = ticks % 4;
        if (lastRepeaterDelay == 0 && ticks > 0) { 
            lastRepeaterDelay = 4;
        }
        
        if (lastRepeaterDelay != 4 && repeaters > 0) { 
             CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Repeaters required for ", "input", " seconds = ", "result", " \nLast repeater tick = ", "result"}, new String[] {seconds}, new String[] {nf.format(repeaters), nf.format(lastRepeaterDelay)});
            return message;
        } else { 
            CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Repeaters required for ", "input", " seconds = ", "result"}, new String[] {seconds}, new String[] {nf.format(repeaters)});
            return message;
        }
    }

    public static String helpMessage = """
        §b§LSeconds to Repeater:§r§f
        Calculates repeaters needed for a given redstone signal duration.
        Base command §e/calc secondstorepeater§r or §e/calc secondstorepeater help§r shows this message.
        
        §eUsage: /calc secondstorepeater convert <seconds>§f
          <seconds>: Duration in seconds (can be an expression, must be non-negative).
          Example: /calc secondstorepeater convert 10.5
                """;
}
