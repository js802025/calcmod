package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class Storage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("storage").then(Commands.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })
        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(Commands.argument("itemsperhour", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("storage").then(CommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
//        .executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })
//        .then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })))
//        .then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
//        .executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }))
//        .then(CommandManager.literal("help").executes((ctx) -> {
//            CalcMessageBuilder message = Help.execute("storage");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour);
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Required ","input","xHopper speed <gray>(9,000/hr)<white> sorters for ", "input"," items/hr = ", "result", " \nSBs/hr = ", "result"}, new String[] {nf.format(timesHopperSpeed), itemsperhour}, new String[] {nf.format(sorters), nf.format(sbsperhour)});
        
        return message;
    }

    public static String helpMessage = """
        <aqua><bold>Storage:<reset><white>
        Calculates the number of needed item sorters given a rate of items per hour <gray><italic>(can be in expression form)<reset><white>. Additional input for multiple times hopper speed sorters.
                <yellow>Usage: /calc storage <itemsperhour>
                Usage: /calc storage <timesHopperSpeed> <itemsperhour><white>
                """;
}
