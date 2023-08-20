package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), ctx.getSource().getPlayerOrException().getOnPos());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getPlayerOrException(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })).then(Commands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("overworld");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(ServerPlayer player, BlockPos... pos) {
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
