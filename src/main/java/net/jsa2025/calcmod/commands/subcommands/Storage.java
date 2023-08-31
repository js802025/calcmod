package net.jsa2025.calcmod.commands.subcommands;


import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.ICommandSender;

public class Storage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("storage").then(Commands.argument("timesHopperSpeed", IntegerArgumentType.integer())
//        .executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })
//        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))
//        .then(Commands.literal("help").executes((ctx) -> {
//            String[] message = Help.execute("storage");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//        return command;
//    }

    public static String[] execute(ICommandSender sender, String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), itemsperhour);
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        String[] message = {"Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): ", nf.format(sorters), " \nSbs per hour: ", nf.format(sbsperhour)};

        return message;
    }

    public static String helpMessage = "§b§LStorage:§r§f \nCalculates the number of needed item sorters given a rate of items per hour §7§o(can be in expression form)§r§f. Additional input for multiple times hopper speed sorters. \n§eUsage: /calc storage <itemsperhour> \nUsage: /calc storage <timesHopperSpeed> <itemsperhour>§f";
}
