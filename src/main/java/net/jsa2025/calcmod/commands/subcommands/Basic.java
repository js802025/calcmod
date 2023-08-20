package net.jsa2025.calcmod.commands.subcommands;



import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.commands.CalcCommand;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;


public class Basic {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US")); 


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.argument("expression", StringArgumentType.greedyString()).executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "expression"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }));
        return command;
    }

    public static String[] execute(ServerPlayer player, String expression) {
        double result = CalcCommand.getParsedExpression(player.getOnPos(), expression);
        String[] message = {expression+" = ", nf.format(result)};
        return message;
    }
}
