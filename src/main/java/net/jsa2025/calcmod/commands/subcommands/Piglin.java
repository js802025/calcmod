package net.jsa2025.calcmod.commands.subcommands;


import net.jsa2025.calcmod.commands.CalcCommand;
import net.minecraft.command.ICommandSender;

import net.minecraft.command.Commands;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;


import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Piglin {

    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    public static Map<String, Double> barter;

    static {
        barter = new HashMap<>();
        barter.put("soul_speed_book", 91.8);
        barter.put("soul_speed_boots", 57.375);
        barter.put("splash_fire_potion", 57.375);
        barter.put("fire_potion", 57.375);
        barter.put("iron_nugget", 1.996);
        barter.put("ender_pearl", 15.3);
        barter.put("string", 3.825);
        barter.put("nether_quartz", 2.7);
        barter.put("obsidian", 11.475);
        barter.put("crying_obsidian", 5.7375);
        barter.put("fire_charge", 11.475);
        barter.put("leather", 3.825);
        barter.put("soul_sand", 2.295);
        barter.put("nether_brick", 2.295);
        barter.put("spectral_arrow", 1.275);
        barter.put("gravel", 0.96);
        barter.put("blackstone", 0.96);
    }

//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//      command
//                .then(Commands.literal("piglin")
//                        .then(Commands.argument("gold", IntegerArgumentType.integer())
//                                .then(Commands.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
//                        .executes((ctx) -> {
//                            Integer gold = IntegerArgumentType.getInteger(ctx, "gold");
//                            String item = StringArgumentType.getString(ctx, "item");
//                            String[] message = execute(ctx.getSource().getEntity(), gold, item);
//                            CalcCommand.sendMessageServer(ctx.getSource(), message);
//                            return 0;
//                        }))).then(Commands.literal("help").executes((ctx) -> {
//                    String[] message = Help.execute("nether");
//                    CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//                    return 0;
//                })));
//      return command;
//    }

    public static CalcMessageBuilder executeToItems(ICommandSender sender, String gold, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), gold)/barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Avg amount of ", "input", " that ", "input"," gold ingots will get = ","result"}, new String[] {item, gold}, new String[] {nf.format(amount_of_items)});
        return message;


    }
    public static CalcMessageBuilder executeToGold(ICommandSender sender, String numberofitems, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), numberofitems)*barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Avg gold ingots to get ", "input"," ","input"," = ", "result"}, new String[] {numberofitems, item}, new String[] {nf.format(amount_of_items)});
        return message;


    }

    public static String helpMessage = "§b§LBarter:§r§f \nCalculates the average amount of gold ingots to barter to get a number of a desired item §7§o(togold)§r§f, or the average amount of an item that will be recieved when bartering a number of gold ingots §7§o(toitem)§r§f. \n§eUsage: /calc barter togold <numberofitems> <item>§f \n§eUsage: /calc barter toitem <amountofgold> <item>§f";
}
