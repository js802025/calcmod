package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class SbToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("sbtoitem").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofsbs"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofsbs"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("sbtoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayer player, String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(player.getOnPos(), numberofsbs, stackSize);
        double items = sbs * stackSize * 27;
        String message[] = {"Items: ", nf.format(items)};
        return message;
    }

    public static String helpMessage = """
        §LSb to Item:§r
            Given a number of sbs (can be in expression form), returns the number of items
            §cUsage: /calc sbtoitem <numberofsbs>§f
                """;
    
}
