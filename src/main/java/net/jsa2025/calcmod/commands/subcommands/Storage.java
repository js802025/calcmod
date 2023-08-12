package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class Storage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("storage").then(ClientCommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes((ctx) -> {
            String[] message = execute(String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })
        .then(ClientCommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "itemsperhour"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("help").executes((ctx) -> {
            String[] message = Help.execute("storage");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("storage").then(CommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes((ctx) -> {
            String[] message = execute(String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })
        .then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(StringArgumentType.getString(ctx, "itemsperhour"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("help").executes((ctx) -> {
            String[] message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static String[] execute(String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(itemsperhour);
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        String[] message = {"Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): ", nf.format(sorters), " \nSbs per hour: ", nf.format(sbsperhour)};
        
        return message;
    }

    public static String helpMessage = """
        §b§LStorage:§r§f
        Calculates the number of needed item sorters given a rate of items per hour §7§o(can be in expression form)§r§f. Additional input for multiple times hopper speed sorters.
                §eUsage: /calc storage <itemsperhour>
                Usage: /calc storage <timesHopperSpeed> <itemsperhour>§f
                """;
}
