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


public class SbToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("sbtoitem").then(ClientCommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "numberofsbs"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("16s").then(ClientCommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "numberofsbs"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.literal("1s").then(ClientCommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "numberofsbs"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            String[] message = Help.execute("sbtoitem");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("sbtoitem").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "numberofsbs"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("16s").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "numberofsbs"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("1s").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "numberofsbs"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("help").executes(ctx -> {
            String[] message = Help.execute("sbtoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static String[] execute(String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(numberofsbs, stackSize);
        double items = sbs * stackSize * 27;
        String message[] = {"Items: ", nf.format(items)};
        return message;
    }

    public static String helpMessage = """
        §b§LSb to Item:§r§f
            Given a number of full Shulker Boxes §7§o(can be in expression form)§r§f, returns the number of items.
            §eUsage: /calc sbtoitem <numberofsbs>§f
                """;
    
}
