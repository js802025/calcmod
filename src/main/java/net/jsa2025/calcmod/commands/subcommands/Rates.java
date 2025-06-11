package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import org.bukkit.entity.Entity;

public class Rates {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("rates").then(Commands.argument("numberofitems", StringArgumentType.string())
        .then(Commands.argument("time", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(Commands.literal("help").executes(ctx ->{
            CalcMessageBuilder message = Help.execute("rates");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));

        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("rates").then(CommandManager.argument("numberofitems", StringArgumentType.string())
//        .then(CommandManager.argument("time", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), StringArgumentType.getString(ctx, "time"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })))
//        .then(CommandManager.literal("help").executes(ctx ->{
//            CalcMessageBuilder message = Help.execute("rates");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, String numberofitems, String time) {
        double items = CalcCommand.getParsedExpression(player, numberofitems);
        double timeDouble = CalcCommand.getParsedExpression(player, time);
        double itemspersecond = items / timeDouble;
        double rates = itemspersecond * 3600;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " Items in ", "input", " Seconds = ", "result", "/hr"}, new String[] {numberofitems, time}, new String[] {nf.format(rates)});
        return message;
    }

    public static String helpMessage = """
        §b§LRates:§r§f
            Given a number of items and afk time in seconds §7§o(can be in expression form)§r§f, returns the number of items per hour.
            §eUsage: /calc rates <numberofitems> <time>§f
                """;
}
