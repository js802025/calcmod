package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.BarterSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.CBarterSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

import java.text.NumberFormat;
import java.util.Locale;

public class Piglin {

    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
                .then(ClientCommands.literal("barter")
                        .then(ClientCommands.literal("toitem")
                        .then(ClientCommands.argument("gold", StringArgumentType.string())
                                .then(ClientCommands.argument("item", StringArgumentType.string()).suggests(new CBarterSuggestionProvider())
                        .executes((ctx) -> {
                            String gold = StringArgumentType.getString(ctx, "gold");
                            String item = StringArgumentType.getString(ctx, "item");
                            CalcMessageBuilder message = executeToItems(ctx.getSource().getEntity(), gold, item);
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        }))))
                        .then(ClientCommands.literal("togold")
                                .then(ClientCommands.argument("numberofitems", StringArgumentType.string())
                                        .then(ClientCommands.argument("item", StringArgumentType.string()).suggests(new CBarterSuggestionProvider())
                                                .executes((ctx) -> {
                                                    String gold = StringArgumentType.getString(ctx, "numberofitems");
                                                    String item = StringArgumentType.getString(ctx, "item");
                                                    CalcMessageBuilder message = executeToGold(ctx.getSource().getEntity(), gold, item);
                                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                                    return 1;
                                                }))))
                        .then(ClientCommands.literal("help").executes((ctx) -> {
                    CalcMessageBuilder message = Help.execute("barter");
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                })));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
                .then(Commands.literal("barter")
                        .then(Commands.literal("toitem")
                        .then(Commands.argument("gold", StringArgumentType.string())
                                .then(Commands.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                                        .executes((ctx) -> {
                                            String gold = StringArgumentType.getString(ctx, "gold");
                                            String item = StringArgumentType.getString(ctx, "item");
                                            CalcMessageBuilder message = executeToItems(ctx.getSource().getEntity(), gold, item);
                                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                                            return 1;
                                        }))))
                        .then(Commands.literal("togold")
                                .then(Commands.argument("numberofitems", StringArgumentType.string())
                                        .then(Commands.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                                                .executes((ctx) -> {
                                                    String gold = StringArgumentType.getString(ctx, "numberofitems");
                                                    String item = StringArgumentType.getString(ctx, "item");
                                                    CalcMessageBuilder message = executeToGold(ctx.getSource().getEntity(), gold, item);
                                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                                    return 1;
                                                }))))
                        .then(Commands.literal("help").executes((ctx) -> {
                            CalcMessageBuilder message = Help.execute("barter");
                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                            return 1;
                        })));
        return command;
    }

    public static CalcMessageBuilder executeToItems(Entity player, String gold, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(player, gold)/CBarterSuggestionProvider.barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Avg amount of ", "input", " that ", "input"," gold ingots will get = ","result"}, new String[] {item, gold}, new String[] {nf.format(amount_of_items)});
        return message;


    }
    public static CalcMessageBuilder executeToGold(Entity player, String numberofitems, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(player, numberofitems)*CBarterSuggestionProvider.barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Avg gold ingots to get ", "input"," ","input"," = ", "result"}, new String[] {numberofitems, item}, new String[] {nf.format(amount_of_items)});
        return message;


    }

    public static String helpMessage = """
            §b§LBarter:§r§f
                Calculates the average amount of gold ingots to barter to get a number of a desired item §7§o(togold)§r§f, or the average amount of an item that will be recieved when bartering a number of gold ingots §7§o(toitem)§r§f.   
                §eUsage: /calc barter togold <numberofitems> <item>§f
                §eUsage: /calc barter toitem <amountofgold> <item>§f
                    """;
}
