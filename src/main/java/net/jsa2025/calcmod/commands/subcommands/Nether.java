package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;

import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Nether {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("nether").executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor().getLocation().toBlock());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }).then(Commands.argument("pos", ArgumentTypes.blockPosition())
        .executes((ctx) -> {
            BlockPosition pos = ctx.getArgument("pos", BlockPositionResolver.class).resolve(ctx.getSource());
            CalcMessageBuilder message = execute(pos);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("nether");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("nether").executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity().getBlockPos());
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
//        .executes((ctx) -> {
//            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
//            CalcMessageBuilder message = execute(pos);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })).then(CommandManager.literal("help").executes((ctx) -> {
//            CalcMessageBuilder message = Help.execute("nether");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(BlockPosition position) {

        CalcMessageBuilder message = new CalcMessageBuilder().addInput("X: "+nf.format(position.toVector().getX())+" Z: "+nf.format(position.toVector().getZ())).addString(" §7>>§f Nether = ").addResult("X: "+nf.format(position.toVector().getX()/8)+" Z: "+nf.format(position.toVector().getZ()/8));
        return message;
    }

    public static String helpMessage = """
        §b§LNether:§r§f
            Given a block position in the Overworld, returns the Nether's corresponding coordinates. If no coordinates are given, command assumes current player position.
            §eUsage: /calc nether <x> <y> <z>§f
                """;
}
