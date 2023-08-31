package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


public class ItemToSb {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("itemtosb").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 16);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 1);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player, numberofitems, stackSize);
        double sbs = items / (stackSize * 27);
        CalcMessageBuilder message= new CalcMessageBuilder().addInput(numberofitems).addString(" Items = ").addResult(nf.format(sbs)).addString(" SBs");

        return message;
    }

    public static String helpMessage = "§b§LItem to Sb:§r§f \nGiven a number of items §7§o(can be in expression form)§r§f, returns the number of Shulker Boxes. \n§eUsage: /calc itemtosb <numberofitems>§f";
}
