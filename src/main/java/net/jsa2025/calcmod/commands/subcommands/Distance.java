package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class Distance {
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("dist").then(Commands.argument("pos1", ArgumentTypes.blockPosition()).then(Commands.argument("pos2", ArgumentTypes.blockPosition()).executes(
                ctx -> {
                    BlockPosition pos1 = ctx.getArgument("pos1", BlockPositionResolver.class).resolve(ctx.getSource());
                    BlockPosition pos2 = ctx.getArgument("pos2", BlockPositionResolver.class).resolve(ctx.getSource());

                    CalcMessageBuilder message = execute(pos1, pos2);
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }
        )).executes(ctx -> {
                BlockPosition pos1 = ((Player)ctx.getSource().getExecutor()).getLocation().toBlock();
                BlockPosition pos2 = ctx.getArgument("pos1", BlockPositionResolver.class).resolve(ctx.getSource());
                CalcMessageBuilder message = execute(pos1, pos2);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
                }
        )).then(Commands.literal("3d").then(Commands.argument("pos1", ArgumentTypes.blockPosition()).then(Commands.argument("pos2", ArgumentTypes.blockPosition()).executes(
                                ctx -> {
                                    BlockPosition pos1 = ctx.getArgument("pos1", BlockPositionResolver.class).resolve(ctx.getSource());
                                    BlockPosition pos2 = ctx.getArgument("pos2", BlockPositionResolver.class).resolve(ctx.getSource());
                                    CalcMessageBuilder message = execute3d(pos1, pos2);
                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                    return 1;
                                }
                        )).executes(ctx -> {
                            BlockPosition pos1 = ((Player)ctx.getSource().getExecutor()).getLocation().toBlock();
                            BlockPosition pos2 = ctx.getArgument("pos1", BlockPositionResolver.class).resolve(ctx.getSource());
                                    CalcMessageBuilder message = execute3d(pos1, pos2);
                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                    return 1;
                                }
                        )))
                        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("dist");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
                }))

        );
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command.then(CommandManager.literal("dist").then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos()).then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos()).executes(
//                ctx -> {
//                    BlockPos pos1 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
//                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos2");
//                    CalcMessageBuilder message = execute(pos1, pos2);
//                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                    return 1;
//                }
//        )).executes(ctx -> {
//                    BlockPos pos1 = ctx.getSource().getPlayer().getBlockPos();
//                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
//                    CalcMessageBuilder message = execute(pos1, pos2);
//                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                    return 1;
//                }
//        )).then(CommandManager.literal("3d").then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos()).then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos()).executes(
//                ctx -> {
//                    BlockPos pos1 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
//                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos2");
//                    CalcMessageBuilder message = execute3d(pos1, pos2);
//                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                    return 1;
//                }
//        )).executes(ctx -> {
//                    BlockPos pos1 = ctx.getSource().getPlayer().getBlockPos();
//                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
//                    CalcMessageBuilder message = execute3d(pos1, pos2);
//                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                    return 1;
//                }
//        ))).then(CommandManager.literal("help").executes(ctx -> {
//            CalcMessageBuilder message = Help.execute("dist");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(BlockPosition pos1, BlockPosition pos2) {
        double dist = Math.sqrt(Math.pow(pos1.toVector().getBlockX()-pos2.toVector().getBlockX(), 2) + Math.pow(pos1.toVector().getBlockX()-pos2.toVector().getBlockZ(), 2));
        CalcMessageBuilder message = new CalcMessageBuilder().addString("Distance from ").addInput("X: "+pos1.toVector().getBlockX()+" Z: "+pos1.toVector().getBlockZ()).addString(" to ").addInput("X: "+pos2.toVector().getBlockX()+" Z: "+pos2.toVector().getBlockZ()).addString(": ").addResult(String.valueOf(nf.format(dist)));
        return message;
    }

    public static CalcMessageBuilder execute3d(BlockPosition pos1, BlockPosition pos2) {
        double dist = Math.sqrt(Math.pow(pos1.toVector().getBlockX()-pos2.toVector().getBlockZ(), 2) + Math.pow(pos1.toVector().getBlockY()-pos2.toVector().getBlockY(), 2) + Math.pow(pos1.toVector().getBlockZ()-pos2.toVector().getBlockZ(), 2));
        CalcMessageBuilder message = new CalcMessageBuilder().addString("Distance from ").addInput("X: "+pos1.toVector().getBlockX()+" Y: "+pos1.toVector().getBlockY()+" Z: "+pos1.toVector().getBlockZ()).addString(" to ").addInput("X: "+pos2.toVector().getBlockX()+" Y: "+pos2.toVector().getBlockY()+" Z: "+pos2.toVector().getBlockZ()).addString(": ").addResult(String.valueOf(nf.format(dist)));
        return message;
    }

    public static String helpMessage = """
        <aqua><bold>Distance:<reset><white>
            Given two block positions, gives distance between them. If only one position is given, uses player's location. The 3D mode provides distance including height.
            <yellow>Usage: /calc dist <x1> <y1> <z1><white>
            <yellow>Usage: /calc dist <x1> <y1> <z1> <x2> <y2> <z2><white>
            <yellow>Usage: /calc dist 3d <x1> <y1> <z1><white>
            <yellow>Usage: /calc dist 3d <x1> <y1> <z1> <x2> <y2> <z2><white>
                """;
}
