package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommands;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;

public class SbToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("sbtoitem").then(ClientCommands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        }))
        .then(ClientCommands.literal("16s").then(ClientCommands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })))
        .then(ClientCommands.literal("1s").then(ClientCommands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })))
        .then(ClientCommands.literal("help").executes(ctx -> {
            String[] message = Help.execute("sbtoitem");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("sbtoitem").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 1);
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

    public static String[] execute(ICommandSender sender, String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), numberofsbs, stackSize);
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
