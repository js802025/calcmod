package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import net.minecraft.entity.Entity;

public class Storage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("storage").then(Commands.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })
        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(Entity player, String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(player.getPosition(), itemsperhour);
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        String[] message = {"Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): ", nf.format(sorters), " \nSbs per hour: ", nf.format(sbsperhour)};
        
        return message;
    }

    public static String helpMessage = "§LStorage:§r \nGiven rate in terms of items per hour(can be in expression form) and optionally hopper speed, returns the number of needed sorters and rates in terms of sbs per hour \n§cUsage: /calc storage <itemsperhour> \nUsage: /calc storage <timesHopperSpeed> <itemsperhour> §f";
}
