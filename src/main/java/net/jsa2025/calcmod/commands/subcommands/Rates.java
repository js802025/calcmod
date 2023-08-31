package net.jsa2025.calcmod.commands.subcommands;


import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.command.ICommandSender;


public class Rates {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("rates").then(Commands.argument("numberofitems", StringArgumentType.string())
//        .then(Commands.argument("time", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("help").executes(ctx ->{
//            String[] message = Help.execute("rates");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//
//        return command;
//    }

    public static String[] execute(ICommandSender sender, String numberofitems, String time) {
        double items = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), numberofitems);
        double timeDouble = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), time);
        double itemspersecond = items / timeDouble;
        double rates = itemspersecond * 3600;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " Items in ", "input", " Seconds = ", "result", "/hr"}, new String[] {numberofitems, time}, new String[] {nf.format(rates)});
        return message;
    }

    public static String helpMessage = "§b§LRates:§r§f \nGiven a number of items and afk time in seconds §7§o(can be in expression form)§r§f, returns the number of items per hour. \n§eUsage: /calc rates <numberofitems> <time>§f ";
}
