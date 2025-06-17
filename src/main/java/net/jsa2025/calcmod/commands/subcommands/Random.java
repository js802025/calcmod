package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import org.bukkit.entity.Entity;

public class Random {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("random")
        .then(Commands.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(Commands.literal("minmax").then(Commands.argument("min", StringArgumentType.string()).then(Commands.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("random");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("random")
//        .then(CommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "max"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }))
//        .then(CommandManager.literal("minmax").then(CommandManager.argument("min", StringArgumentType.string()).then(CommandManager.argument("max", StringArgumentType.greedyString()).executes(ctx -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(),  StringArgumentType.getString(ctx, "min"), StringArgumentType.getString(ctx, "max"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }))))
//        .then(CommandManager.literal("help").executes(ctx -> {
//            CalcMessageBuilder message = Help.execute("random");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }


    public static CalcMessageBuilder execute(Entity player, String... range) {
        if (range.length == 1) {
        double maxInt = CalcCommand.getParsedExpression(player, range[0]);
        String random = nf.format(ThreadLocalRandom.current().nextInt(0, (int) maxInt + 1));
        return new CalcMessageBuilder().addFromArray(new String[] { "Random number between 0 and ", "input", " <gray>(inclusive)<white> = ", "result" }, range, new String[] {random});
        } else if (range.length == 2 ) {
            double max = CalcCommand.getParsedExpression(player, range[1]);
            double min = CalcCommand.getParsedExpression(player, range[0]);
            String random = nf.format(ThreadLocalRandom.current().nextInt((int) min, (int) max + 1));
            return new CalcMessageBuilder().addFromArray(new String[] { "Random number between ", "input", " and ", "input", " <gray>(inclusive)<white> = ", "result" }, range, new String[] {random});

        }
        return new CalcMessageBuilder("Invalid Arguments");
    }

    public static String helpMessage = """
        <aqua><bold>Random:<reset><white>
            Given a maximum and/or minimum value, returns a random number between those values <gray>(inclusive)<reset>. If just a maximum value is entered, picks a random number from 0 to the max value <gray>(inclusive)<reset>.
            <yellow>Usage: /calc random <max><white>
            """;
    
}
