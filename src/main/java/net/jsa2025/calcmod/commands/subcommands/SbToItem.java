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


public class SbToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("sbtoitem").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(Commands.literal("16s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofsbs"), 16);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(Commands.literal("1s").then(Commands.argument("numberofsbs", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "numberofsbs"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("sbtoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("sbtoitem").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }))
//        .then(CommandManager.literal("16s").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 16);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })))
//        .then(CommandManager.literal("1s").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })))
//        .then(CommandManager.literal("help").executes(ctx -> {
//            CalcMessageBuilder message = Help.execute("sbtoitem");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(player, numberofsbs, stackSize);
        double items = sbs * stackSize * 27;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " SBs = ", "result", " Items"}, new String [] {numberofsbs}, new String[] {nf.format(items)});
        return message;
    }

    public static String helpMessage = """
        <aqua><bold>Sb to Item:<reset><white>
            Given a number of full Shulker Boxes <gray><italic>(can be in expression form)<reset><white>, returns the number of items.
            <yellow>Usage: /calc sbtoitem <numberofsbs><white>
                """;
    
}
