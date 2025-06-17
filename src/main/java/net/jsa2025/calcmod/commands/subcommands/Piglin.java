package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.BarterSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import org.bukkit.entity.Entity;


import java.text.NumberFormat;
import java.util.Locale;

public class Piglin {

    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
                .then(Commands.literal("barter")
                        .then(Commands.literal("toitem")
                        .then(Commands.argument("gold", StringArgumentType.string())
                                .then(Commands.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                        .executes((ctx) -> {
                            String gold = StringArgumentType.getString(ctx, "gold");
                            String item = StringArgumentType.getString(ctx, "item");
                            CalcMessageBuilder message = executeToItems(ctx.getSource().getExecutor(), gold, item);
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        }))))
                        .then(Commands.literal("togold")
                                .then(Commands.argument("numberofitems", StringArgumentType.string())
                                        .then(Commands.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                                                .executes((ctx) -> {
                                                    String gold = StringArgumentType.getString(ctx, "numberofitems");
                                                    String item = StringArgumentType.getString(ctx, "item");
                                                    CalcMessageBuilder message = executeToGold(ctx.getSource().getExecutor(), gold, item);
                                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                                    return 1;
                                                }))))
                        .then(Commands.literal("help").executes((ctx) -> {
                    CalcMessageBuilder message = Help.execute("barter");
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//                .then(CommandManager.literal("barter")
//                        .then(CommandManager.literal("toitem")
//                        .then(CommandManager.argument("gold", StringArgumentType.string())
//                                .then(CommandManager.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
//                                        .executes((ctx) -> {
//                                            String gold = StringArgumentType.getString(ctx, "gold");
//                                            String item = StringArgumentType.getString(ctx, "item");
//                                            CalcMessageBuilder message = executeToItems(ctx.getSource().getEntity(), gold, item);
//                                            CalcCommand.sendMessageServer(ctx.getSource(), message);
//                                            return 1;
//                                        }))))
//                        .then(CommandManager.literal("togold")
//                                .then(CommandManager.argument("numberofitems", StringArgumentType.string())
//                                        .then(CommandManager.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
//                                                .executes((ctx) -> {
//                                                    String gold = StringArgumentType.getString(ctx, "numberofitems");
//                                                    String item = StringArgumentType.getString(ctx, "item");
//                                                    CalcMessageBuilder message = executeToGold(ctx.getSource().getEntity(), gold, item);
//                                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                                                    return 1;
//                                                }))))
//                        .then(CommandManager.literal("help").executes((ctx) -> {
//                            CalcMessageBuilder message = Help.execute("barter");
//                            CalcCommand.sendMessageServer(ctx.getSource(), message);
//                            return 1;
//                        })));
//        return command;
//    }

    public static CalcMessageBuilder executeToItems(Entity player, String gold, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(player, gold)/BarterSuggestionProvider.barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Avg amount of ", "input", " that ", "input"," gold ingots will get = ","result"}, new String[] {item, gold}, new String[] {nf.format(amount_of_items)});
        return message;


    }
    public static CalcMessageBuilder executeToGold(Entity player, String numberofitems, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(player, numberofitems)*BarterSuggestionProvider.barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Avg gold ingots to get ", "input"," ","input"," = ", "result"}, new String[] {numberofitems, item}, new String[] {nf.format(amount_of_items)});
        return message;


    }

    public static String helpMessage = """
            <aqua><bold>Barter:<reset><white>
                Calculates the average amount of gold ingots to barter to get a number of a desired item <gray><italic>(togold)<reset><white>, or the average amount of an item that will be recieved when bartering a number of gold ingots <gray><italic>(toitem)<reset><white>.   
                <yellow>Usage: /calc barter togold <numberofitems> <item><white>
                <yellow>Usage: /calc barter toitem <amountofgold> <item><white>
                    """;
}
