package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
//import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().getBlockPos());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }).then(ClientCommandManager.argument("pos", CBlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = CBlockPosArgumentType.getCBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(ClientCommandManager.literal("help").executes((ctx) -> {
            String[] message = Help.execute("overworld");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().getBlockPos());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(CommandManager.literal("help").executes((ctx) -> {
            String[] message = Help.execute("overworld");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static String[] execute(Entity player, BlockPos... pos) {
        BlockPos position;
        position = pos[0];
        

        String[] message = {"Overworld Coords: ", "X: "+nf.format(position.getX()*8)+" Z: "+nf.format(position.getZ()*8)};
        return message;
    }

    public static String helpMessage = """
        §LOverworld:§r
            Given a block position in the nether, returns the overworld coordinates
            §cUsage: /calc overworld <x> <y> <z>§f
                """;


}
