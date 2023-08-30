package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;
//import net.minecraft.core.BlockPos;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Nether {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("nether").executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity().getBlockPos());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }).then(ClientCommandManager.argument("pos", CBlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = CBlockPosArgumentType.getCBlockPos(ctx, "pos");
            CalcMessageBuilder message = execute(pos);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(ClientCommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("nether");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("nether").executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity().getBlockPos());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
            CalcMessageBuilder message = execute(pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(CommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("nether");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(BlockPos... pos) {
        BlockPos position;
        position = pos[0];
        CalcMessageBuilder message = new CalcMessageBuilder().addInput("X: "+nf.format(position.getX())+" Z: "+nf.format(position.getZ())).addString(" §7>>§f Nether = ").addResult("X: "+nf.format(position.getX()/8)+" Z: "+nf.format(position.getZ()/8));
        return message;
    }

    public static String helpMessage = """
        §b§LNether:§r§f
            Given a block position in the Overworld, returns the Nether's corresponding coordinates. If no coordinates are given, command assumes current player position.
            §eUsage: /calc nether <x> <y> <z>§f
                """;
}
