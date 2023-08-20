package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommands;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
public class StackToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("stacktoitem").then(ClientCommands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        }))
        .then(ClientCommands.literal("16s").then(ClientCommands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofstacks"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })))
        .then(ClientCommands.literal("1s").then(ClientCommands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "numberofstacks"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })))
        .then(ClientCommands.literal("help").executes(ctx -> {
            String[] message = Help.execute("stacktoitem");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
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

    public static String[] execute(ServerPlayer player, String numberofstacks, int stackSize) {
        double stacks = CalcCommand.getParsedExpression(player.getOnPos(), numberofstacks, 1);
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
