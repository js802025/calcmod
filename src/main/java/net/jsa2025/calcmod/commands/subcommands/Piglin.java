package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.BarterSuggestionProvider;


import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.text.NumberFormat;
import java.util.Locale;


public class Piglin {

    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
      command
                .then(Commands.literal("piglin")
                        .then(Commands.argument("gold", IntegerArgumentType.integer())
                                .then(Commands.argument("item", StringArgumentType.string()).suggests(new BarterSuggestionProvider())
                        .executes((ctx) -> {
                            Integer gold = IntegerArgumentType.getInteger(ctx, "gold");
                            String item = StringArgumentType.getString(ctx, "item");
                            String[] message = execute(ctx.getSource().getPlayer(), gold, item);
                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                            return 0;
                        }))).then(Commands.literal("help").executes((ctx) -> {
                    String[] message = Help.execute("nether");
                    CalcCommand.sendMessageServer(ctx.getSource(), message, true);
                    return 0;
                })));
      return command;
    }

    public static String[] execute(ServerPlayer player, Integer gold, String item) {
        double amount_of_items = gold/BarterSuggestionProvider.barter.get(item);
        String[] message = {"Number of "+item+"s that "+gold+" gold will get: \nResult: ", String.valueOf(nf.format(amount_of_items))};
        return message;


    }

    public static String helpMessage = """
        §LNether:§r
            Given a block position in the overworld, returns the nether coordinates
            §cUsage: /calc nether <x> <y> <z>§f
                """;
}
