package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
public class Nether {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("nether").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException().getEntity().getCommandSenderBlockPosition());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
            String[] message = execute(pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })).then(Commands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("nether");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static String[] execute(BlockPos... pos) {
        BlockPos position;
        position = pos[0];
        String[] message = {"Nether Coords: ", "X: "+nf.format(position.getX()/8)+" Z: "+nf.format(position.getZ()/8)};
        return message;
    }

    public static String helpMessage = "§LNether:§r \nGiven a block position in the overworld, returns the nether coordinates \n§cUsage: /calc nether <x> <y> <z>§f";
}
