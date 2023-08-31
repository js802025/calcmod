package net.jsa2025.calcmod.commands.subcommands;



import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.ICommandSender;

import net.minecraft.command.Commands;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


public class StackToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("stacktoitem").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))
//        .then(Commands.literal("16s").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 16);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("1s").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("help").executes(ctx -> {
//            String[] message = Help.execute("stacktoitem");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//        return command;
//    }

    public static String[] execute(ICommandSender sender, String numberofstacks, int stackSize) {
        double stacks = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), numberofstacks, 1);
        double items = stacks * stackSize;
        return new CalcMessageBuilder().addInput(numberofstacks).addString(" ").addInput(nf.format(stackSize)).addString(" Stacks = ").addResult(nf.format(items)).addString(" Items");
    }

    public static String helpMessage = "§b§LStack to Item:§r§f \nGiven a number of stacks §7§o(can be in expression form)§r§f, returns the number of items. \n§eUsage: /calc stacktoitem <numberofstacks>§f";
    
}
