package net.jsa2025.calcmod.commands.subcommands;


import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.commands.CalcCommand;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;





public class Basic {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US")); 
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.argument("expression", StringArgumentType.greedyString()).executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "expression"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.argument("expression", StringArgumentType.greedyString()).executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "expression"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));
        return command;
    }

    public static String[] execute(PlayerEntity player, String expression) {
        double result = CalcCommand.getParsedExpression(player.getBlockPos(), expression);
        String[] message = {expression+" = ", nf.format(result)};
        return message;
    }
}
