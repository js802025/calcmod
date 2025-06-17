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

public class StackToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("stacktoitem").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofstacks"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofstacks"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("stacktoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("stacktoitem").then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 64);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }))
//        .then(CommandManager.literal("16s").then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 16);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })))
//        .then(CommandManager.literal("1s").then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
//        .executes(ctx -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofstacks"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })))
//        .then(CommandManager.literal("help").executes(ctx -> {
//            CalcMessageBuilder message = Help.execute("stacktoitem");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, String numberofstacks, int stackSize) {
        double stacks = CalcCommand.getParsedExpression(player, numberofstacks, 1);
        double items = stacks * stackSize;
        return new CalcMessageBuilder().addInput(numberofstacks).addString(" ").addInput(nf.format(stackSize)).addString(" Stacks = ").addResult(nf.format(items)).addString(" Items");
    }

    public static String helpMessage = """
        <aqua><bold>Stack to Item:<reset><white>
            Given a number of stacks <gray><italic>(can be in expression form)<reset><white>, returns the number of items.
            <yellow>Usage: /calc stacktoitem <numberofstacks><white>
                """;
    
}
