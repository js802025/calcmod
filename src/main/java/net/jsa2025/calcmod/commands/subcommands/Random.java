package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Random {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> randomLiteral) {
        randomLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("random");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        randomLiteral.then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("random");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        randomLiteral.then(ClientCommandManager.literal("range")
                .then(ClientCommandManager.argument("max_value", StringArgumentType.string())
                        .executes(ctx -> {
                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(),
                                    StringArgumentType.getString(ctx, "max_value"));
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        })));

        randomLiteral.then(ClientCommandManager.literal("range_minmax")
                .then(ClientCommandManager.argument("min_value", StringArgumentType.string())
                        .then(ClientCommandManager.argument("max_value", StringArgumentType.string())
                                .executes(ctx -> {
                                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(),
                                            StringArgumentType.getString(ctx, "min_value"),
                                            StringArgumentType.getString(ctx, "max_value"));
                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                    return 1;
                                }))));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> randomLiteral = ClientCommandManager.literal("random");
        populateClient(randomLiteral);
        return randomLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> randomLiteral) {
        randomLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("random");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        randomLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("random");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        randomLiteral.then(CommandManager.literal("range")
                .then(CommandManager.argument("max_value", StringArgumentType.string())
                        .executes(ctx -> {
                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(),
                                    StringArgumentType.getString(ctx, "max_value"));
                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                            return 1;
                        })));

        randomLiteral.then(CommandManager.literal("range_minmax")
                .then(CommandManager.argument("min_value", StringArgumentType.string())
                        .then(CommandManager.argument("max_value", StringArgumentType.string())
                                .executes(ctx -> {
                                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(),
                                            StringArgumentType.getString(ctx, "min_value"),
                                            StringArgumentType.getString(ctx, "max_value"));
                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                    return 1;
                                }))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> randomLiteral = CommandManager.literal("random");
        populateServer(randomLiteral);
        return randomLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String... range) {
        if (range.length == 1) {
            double maxInt = CalcCommand.getParsedExpression(player, range[0]);
            if (maxInt < 0) {
                return new CalcMessageBuilder().addString("Error: Maximum value must be non-negative.");
            }
            String random = nf.format(ThreadLocalRandom.current().nextInt(0, (int) maxInt + 1));
            return new CalcMessageBuilder().addFromArray(
                    new String[] { "Random number between 0 and ", "input", " §7(inclusive)§f = ", "result" },
                    new String[] { range[0] }, new String[] { random });
        } else if (range.length == 2) {
            double val1 = CalcCommand.getParsedExpression(player, range[0]);
            double val2 = CalcCommand.getParsedExpression(player, range[1]);

            double min = Math.min(val1, val2);
            double max = Math.max(val1, val2);

            if (min < 0 && max < 0 && min == max) {
                String randomNum = nf.format(ThreadLocalRandom.current().nextInt((int) min, (int) min + 1));
                return new CalcMessageBuilder()
                        .addFromArray(
                                new String[] { "Random number between ", "input", " and ", "input",
                                        " §7(inclusive)§f = ", "result" },
                                new String[] { range[0], range[1] }, new String[] { randomNum });
            }
            if (max < 0) {
                return new CalcMessageBuilder().addString(
                        "Error: When providing two negative numbers, the largest (closest to zero) must be the second argument or they must be equal.");
            }
            if (min < 0 && max >= 0) { }

            String randomNum = nf.format(ThreadLocalRandom.current().nextInt((int) min, (int) max + 1));
            return new CalcMessageBuilder().addFromArray(new String[] { "Random number between ", "input", " and ",
                    "input", " §7(inclusive)§f = ", "result" }, new String[] { range[0], range[1] },
                    new String[] { randomNum });

        }
        return new CalcMessageBuilder("Invalid arguments. Use help for usage.");
    }

    public static String helpMessage = """
            §b§LRandom:§r§f
            Generates a random integer within a specified range.
            Base command §e/calc random§r or §e/calc random help§r shows this message.

            §eUsage: /calc random range <max_value>§f
              Generates a random number between 0 (inclusive) and <max_value> (inclusive).
              <max_value>: The maximum value (e.g., 100). Must be non-negative.
              Example: /calc random range 50

            §eUsage: /calc random range_minmax <value1> <value2>§f
              Generates a random number between <value1> and <value2> (inclusive).
              The command will determine the min and max from the two values.
              If both values are negative, the largest (closest to zero) should be <value2> or they must be equal.
              Example: /calc random range_minmax 10 20
              Example: /calc random range_minmax -5 5
                """;

}
