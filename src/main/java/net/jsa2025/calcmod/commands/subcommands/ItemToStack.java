package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class ItemToStack {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("itemtostack").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("itemtostack");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayer player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player.getOnPos(), numberofitems, stackSize);
        double stacks = Math.floor(items/stackSize);
        double leftover = items % stackSize;
        String[] message = {"Stacks: ",  nf.format(stacks), " \nLeftover Items: ",  nf.format(leftover)};      
        return message;
    }

    public static String helpMessage = """
        §LItem to Stack:§r
            Given a number of items (can be in expression form), returns the number of stacks and leftover items
            §cUsage: /calc itemtostack <numberofitems>§f
                """;
}
