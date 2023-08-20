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
public class ItemToSb {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("itemtosb").then(ClientCommands.argument("numberofitems", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        }))
        .then(ClientCommands.literal("16s").then(ClientCommands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })))
        .then(ClientCommands.literal("1s").then(ClientCommands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "numberofitems"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })))
        .then(ClientCommands.literal("help").executes(ctx -> {
            String[] message = Help.execute("itemtosb");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("itemtosb").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes((ctx) -> {
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
            String[] message = Help.execute("itemtosb");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayer player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player.getOnPos(), numberofitems, stackSize);
        double sbs = items / (stackSize * 27);
        String[] message= {"Sbs: ", nf.format(sbs)};

        return message;
    }

    public static String helpMessage = """
        §LItem to Sb:§r
            Given a number of items (can be in expression form), returns the number of sbs
            §cUsage: /calc itemtosb <numberofitems>§f
                """;
}
