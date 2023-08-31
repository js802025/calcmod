package net.jsa2025.calcmod.commands.subcommands;



import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.command.ICommandSender;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


public class SbToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("sbtoitem").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))
//        .then(Commands.literal("16s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 16);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("1s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("help").executes(ctx -> {
//            String[] message = Help.execute("sbtoitem");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//        return command;
//    }

    public static String[] execute(ICommandSender sender, String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), numberofsbs, stackSize);
        double items = sbs * stackSize * 27;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " SBs = ", "result", " Items"}, new String [] {numberofsbs}, new String[] {nf.format(items)});
        return message;
    }

    public static String helpMessage = "§b§LSb to Item:§r§f \nGiven a number of full Shulker Boxes §7§o(can be in expression form)§r§f, returns the number of items. \n§eUsage: /calc sbtoitem <numberofsbs>§f";
    
}
