package net.jsa2025.calcmod.commands.subcommands;



import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.ICommandSender;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


public class ItemToStack {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("itemtostack").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))
//        .then(Commands.literal("16s").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 16);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("1s").then(Commands.argument("numberofitems", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("help").executes(ctx -> {
//            String[] message = Help.execute("itemtostack");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(ICommandSender sender, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), numberofitems, stackSize);
        double stacks = Math.floor(items/stackSize);
        double leftover = items % stackSize;
        CalcMessageBuilder message = new CalcMessageBuilder().addInput(numberofitems).addString(" ").addInput(nf.format(stackSize)).addString(" Stackable items = ").addResult(nf.format(stacks)).addString(" Stacks + ").addResult(nf.format(leftover)).addString(" Items");

        return message;
    }

    public static String helpMessage = "§b§LItem to Stack:§r§f \nGiven a number of items §7§o(can be in expression form)§r§f, returns the number of stacks and remainder items. \n§eUsage: /calc itemtostack <numberofitems>§f";
}
