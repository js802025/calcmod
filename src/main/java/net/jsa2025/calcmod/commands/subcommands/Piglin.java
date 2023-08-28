package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.BarterSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.CBarterSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.text.NumberFormat;
import java.util.Locale;

public class Piglin {

    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
                .then(ClientCommandManager.literal("barter")
                        .then(ClientCommandManager.literal("toitem")
                        .then(ClientCommandManager.argument("gold", StringArgumentType.string())
                                .then(ClientCommandManager.argument("item", StringArgumentType.string()).suggests(new CBarterSuggestionProvider())
                        .executes((ctx) -> {
                            String gold = StringArgumentType.getString(ctx, "gold");
                            String item = StringArgumentType.getString(ctx, "item");
                            CalcMessageBuilder message = executeToItems(ctx.getSource().getPlayer(), gold, item);
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        }))))
                        .then(ClientCommandManager.literal("togold")
                                .then(ClientCommandManager.argument("numberofitems", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("item", StringArgumentType.string()).suggests(new CBarterSuggestionProvider())
                                                .executes((ctx) -> {
                                                    String gold = StringArgumentType.getString(ctx, "numberofitems");
                                                    String item = StringArgumentType.getString(ctx, "item");
                                                    CalcMessageBuilder message = executeToGold(ctx.getSource().getPlayer(), gold, item);
                                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                                    return 1;
                                                }))))
                        .then(ClientCommandManager.literal("help").executes((ctx) -> {
                    CalcMessageBuilder message = Help.execute("nether");
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
                .then(CommandManager.literal("barter")
                        .then(CommandManager.literal("toitem")
                        .then(CommandManager.argument("gold", StringArgumentType.string())
                                .then(CommandManager.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                                        .executes((ctx) -> {
                                            String gold = StringArgumentType.getString(ctx, "gold");
                                            String item = StringArgumentType.getString(ctx, "item");
                                            CalcMessageBuilder message = executeToItems(ctx.getSource().getPlayer(), gold, item);
                                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                                            return 1;
                                        }))))
                        .then(CommandManager.literal("togold")
                                .then(CommandManager.argument("numberofitems", StringArgumentType.string())
                                        .then(CommandManager.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                                                .executes((ctx) -> {
                                                    String gold = StringArgumentType.getString(ctx, "numberofitems");
                                                    String item = StringArgumentType.getString(ctx, "item");
                                                    CalcMessageBuilder message = executeToGold(ctx.getSource().getPlayer(), gold, item);
                                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                                    return 1;
                                                }))))
                        .then(CommandManager.literal("help").executes((ctx) -> {
                            CalcMessageBuilder message = Help.execute("nether");
                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                            return 1;
                        })));
        return command;
    }

    public static CalcMessageBuilder executeToItems(PlayerEntity player, String gold, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(player.getBlockPos(), gold)/CBarterSuggestionProvider.barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Amount of ", "input", "s that ", "input"," gold will get: \nResult: ","result"}, new String[] {item, gold}, new String[] {nf.format(amount_of_items)});
        return message;


    }
    public static CalcMessageBuilder executeToGold(PlayerEntity player, String numberofitems, String item) {

        double amount_of_items = CalcCommand.getParsedExpression(player.getBlockPos(), numberofitems)*CBarterSuggestionProvider.barter.get(item);
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Amount of gold that you need to get ", "input",": \nResult: ", "result"}, new String[] {numberofitems}, new String[] {nf.format(amount_of_items)});
        return message;


    }

    public static String helpMessage = """
        §LNether:§r
            Given a block position in the overworld, returns the nether coordinates
            §cUsage: /calc nether <x> <y> <z>§f
                """;
}
