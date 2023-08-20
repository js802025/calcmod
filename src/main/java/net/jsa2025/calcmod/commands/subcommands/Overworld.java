package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().getPosition());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })).then(Commands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("overworld");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(Entity player, BlockPos... pos) {
        BlockPos position;
        position = pos[0];
        

        String[] message = {"Overworld Coords: ", "X: "+nf.format(position.getX()*8)+" Z: "+nf.format(position.getZ()*8)};
        return message;
    }

    public static String helpMessage = "§LOverworld:§r \nGiven a block position in the nether, returns the overworld coordinates \n§cUsage: /calc overworld <x> <y> <z>§f";


}
