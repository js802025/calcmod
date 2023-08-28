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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ItemToStack {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("itemtostack").then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("16s").then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.literal("1s").then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("itemtostack").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("16s").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("1s").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(PlayerEntity player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player.getBlockPos(), numberofitems, stackSize);
        double stacks = Math.floor(items/stackSize);
        double leftover = items % stackSize;
        CalcMessageBuilder message = new CalcMessageBuilder().addString("Number of Stacks(").addInput(String.valueOf(stackSize)).addString(") for ").addInput(numberofitems).addString("items: ").addResult(String.valueOf(stacks)).addString(" \nLeftover Items: ").addResult(String.valueOf(leftover));
        
        return message;
    }

    public static String helpMessage = """
        §b§LItem to Stack:§r§f
            Given a number of items §7§o(can be in expression form)§r§f, returns the number of stacks and remainder items.
            §eUsage: /calc itemtostack <numberofitems>§f
                """;
}
