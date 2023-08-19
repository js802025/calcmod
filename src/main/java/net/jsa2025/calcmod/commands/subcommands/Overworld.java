package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), ctx.getSource().getPlayer().getBlockPos());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getPlayer(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(CommandManager.literal("help").executes((ctx) -> {
            String[] message = Help.execute("overworld");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 1;
        })));
        return command;
    }

    public static String[] execute(PlayerEntity player, BlockPos... pos) {
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
