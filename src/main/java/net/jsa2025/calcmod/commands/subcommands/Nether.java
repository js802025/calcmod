package net.jsa2025.calcmod.commands.subcommands;


import net.minecraft.util.math.BlockPos;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Nether {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("nether").executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity().getPosition());
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
//        .executes((ctx) -> {
//            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
//            String[] message = execute(pos);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })).then(Commands.literal("help").executes((ctx) -> {
//            String[] message = Help.execute("nether");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(BlockPos... pos) {
        BlockPos position;
        position = pos[0];
        CalcMessageBuilder message = new CalcMessageBuilder().addInput("X: "+nf.format(position.getX())+" Z: "+nf.format(position.getZ())).addString(" §7>>§f Nether = ").addResult("X: "+nf.format(position.getX()/8)+" Z: "+nf.format(position.getZ()/8));
        return message;
    }

    public static String helpMessage = "§b§LNether:§r§f \nGiven a block position in the Overworld, returns the Nether's corresponding coordinates. If no coordinates are given, command assumes current player position. \n§eUsage: /calc nether <x> <y> <z>§f";
}
