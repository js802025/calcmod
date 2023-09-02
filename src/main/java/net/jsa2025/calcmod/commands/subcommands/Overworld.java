package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
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
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().getBlockPos());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), pos);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(CommandManager.literal("help").executes((ctx) -> {
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
