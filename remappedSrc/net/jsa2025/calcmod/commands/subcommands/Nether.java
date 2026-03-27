package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
//import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.command.argument.BlockPosArgumentType;

import net.minecraft.server.command.Commands;
import net.minecraft.server.command.ServerCommandSource;

public class Nether {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("nether").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().blockPosition());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }).then(ClientCommands.argument("pos", CBlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = CBlockPosArgumentType.getCBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(ClientCommands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("nether");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(Commands.literal("nether").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().blockPosition());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }).then(Commands.argument("pos", BlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgumentType.blockPosition(ctx, "pos");
            String[] message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(Commands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("nether");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static String[] execute(Entity player, BlockPos... pos) {
        BlockPos position;
        position = pos[0];
        String[] message = {"Nether Coords: ", "X: "+nf.format(position.getX()/8)+" Z: "+nf.format(position.getZ()/8)};
        return message;
    }

    public static String helpMessage = """
        §LNether:§r
            Given a block position in the overworld, returns the nether coordinates
            §cUsage: /calc nether <x> <y> <z>§f
                """;
}
