package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;

public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("overworld").executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().getPosition());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("overworld");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(Entity player, BlockPos... pos) {
        BlockPos position;
        position = pos[0];


        CalcMessageBuilder message = new CalcMessageBuilder().addInput("X: "+nf.format(position.getX())+" Z: "+nf.format(position.getZ())).addString(" §7>>§f Overworld = ").addResult("X: "+nf.format(position.getX()*8)+" Z: "+nf.format(position.getZ()*8));
        return message;
    }

    public static String helpMessage = "§b§LOverworld:§r§f \nGiven a block position in the Nether, returns the Overworld's corresponding coordinates. If no coordinates are given, command assumes current player position. \n§eUsage: /calc overworld <x> <y> <z>§f";


}
