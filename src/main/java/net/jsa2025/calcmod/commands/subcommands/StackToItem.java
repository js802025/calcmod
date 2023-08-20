package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

public class StackToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("stacktoitem").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofstacks"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofstacks"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("stacktoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayerEntity player, String numberofstacks, int stackSize) {
        double stacks = CalcCommand.getParsedExpression(player.getEntity().blockPosition(), numberofstacks, 1);
        double items = stacks * stackSize;
        String[] message = {"Items: ", nf.format(items)};
        return message;
    }

    public static String helpMessage = """
        §LStack to Item:§r
            Given a number of stacks (can be in expression form), returns the number of items
            §cUsage: /calc stacktoitem <numberofstacks>§f
                """;
    
}
