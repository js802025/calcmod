package net.jsa2025.calcmod.commands.subcommands;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.command.ICommandSender;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


public class Random {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("random")
//        .then(Commands.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "max"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))
//        .then(Commands.literal("minmax").then(Commands.argument("min", StringArgumentType.string()).then(Commands.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(),  StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))))
//        .then(Commands.literal("help").executes(ctx -> {
//            String[] message = Help.execute("random");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//        return command;
//    }


    public static CalcMessageBuilder execute(ICommandSender sender, String... range) {
        if (range.length == 1) {
        double maxInt = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), range[0]);
        String random = nf.format(ThreadLocalRandom.current().nextInt(0, (int) maxInt + 1));
        return new CalcMessageBuilder().addFromArray(new String[] { "Random number between 0 and ", "input", " §7(inclusive)§f = ", "result" }, range, new String[] {random});
        } else if (range.length == 2 ) {
            double max = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), range[1]);
            double min = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), range[0]);
            String random = nf.format(ThreadLocalRandom.current().nextInt((int) min, (int) max + 1));
            return new CalcMessageBuilder().addFromArray(new String[] { "Random number between ", "input", " and ", "input", " §7(inclusive)§f = ", "result" }, range, new String[] {random});

        }
        return new CalcMessageBuilder("Invalid Arguments");
    }

    public static String helpMessage = "§b§LRandom:§r§f \nGiven a maximum and/or minimum value, returns a random number between those values §7(inclusive)§r. If just a maximum value is entered, picks a random number from 0 to the max value §7(inclusive)§r. \n§eUsage: /calc random <max>§f";
    
}
