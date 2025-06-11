package net.jsa2025.calcmod.commands.subcommands;




import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import org.bukkit.entity.Entity;


public class Basic {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.argument("expression", StringArgumentType.greedyString()).executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "expression"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        return command;
    }
    
//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.argument("expression", StringArgumentType.greedyString()).executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "expression"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }));
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, String expression) {
        CalcMod.LOGGER.info("Entity Name: "+expression);
        double result = CalcCommand.getParsedExpression(player, expression);
        return new CalcMessageBuilder(CalcMessageBuilder.MessageType.BASIC, new String[] {expression}, new String[] {nf.format(result)});
    }
}
