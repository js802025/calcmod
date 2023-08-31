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
import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.command.Commands;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


public class Storage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("storage").then(Commands.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })
        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(Entity player, String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour);
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Required ","input","xHopper speed §7(9,000/hr)§f sorters for ", "input"," items/hr = ", "result", " \nSBs/hr = ", "result"}, new String[] {nf.format(timesHopperSpeed), itemsperhour}, new String[] {nf.format(sorters), nf.format(sbsperhour)});
        
        return message;
    }

    public static String helpMessage = "§b§LStorage:§r§f \nCalculates the number of needed item sorters given a rate of items per hour §7§o(can be in expression form)§r§f. Additional input for multiple times hopper speed sorters. \n§eUsage: /calc storage <itemsperhour> \nUsage: /calc storage <timesHopperSpeed> <itemsperhour>§f";
}
