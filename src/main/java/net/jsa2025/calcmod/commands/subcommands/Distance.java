package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.text.NumberFormat;
import java.util.Locale;

public class Distance {
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command.then(ClientCommandManager.literal("dist").then(ClientCommandManager.argument("pos1", CBlockPosArgument.blockPos()).then(ClientCommandManager.argument("pos2", CBlockPosArgument.blockPos()).executes(
                ctx -> {
                    BlockPos pos1 = CBlockPosArgument.getBlockPos(ctx, "pos1");
                    BlockPos pos2 = CBlockPosArgument.getBlockPos(ctx, "pos2");
                    CalcMessageBuilder message = execute(pos1, pos2);
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }
        )).executes(ctx -> {
                BlockPos pos1 = ctx.getSource().getPlayer().getBlockPos();
                BlockPos pos2 = CBlockPosArgument.getBlockPos(ctx, "pos1");
                CalcMessageBuilder message = execute(pos1, pos2);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
                }
        )).then(ClientCommandManager.literal("3d").then(ClientCommandManager.argument("pos1", CBlockPosArgument.blockPos()).then(ClientCommandManager.argument("pos2", CBlockPosArgument.blockPos()).executes(
                                ctx -> {
                                    BlockPos pos1 = CBlockPosArgument.getBlockPos(ctx, "pos1");
                                    BlockPos pos2 = CBlockPosArgument.getBlockPos(ctx, "pos2");
                                    CalcMessageBuilder message = execute3d(pos1, pos2);
                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                    return 1;
                                }
                        )).executes(ctx -> {
                                    BlockPos pos1 = ctx.getSource().getPlayer().getBlockPos();
                                    BlockPos pos2 = CBlockPosArgument.getBlockPos(ctx, "pos1");
                                    CalcMessageBuilder message = execute3d(pos1, pos2);
                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                    return 1;
                                }
                        )))
                        .then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("dist");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
                }))

        );
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command.then(CommandManager.literal("dist").then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos()).then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos()).executes(
                ctx -> {
                    BlockPos pos1 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos2");
                    CalcMessageBuilder message = execute(pos1, pos2);
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }
        )).executes(ctx -> {
                    BlockPos pos1 = ctx.getSource().getPlayer().getBlockPos();
                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
                    CalcMessageBuilder message = execute(pos1, pos2);
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }
        )).then(CommandManager.literal("3d").then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos()).then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos()).executes(
                ctx -> {
                    BlockPos pos1 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos2");
                    CalcMessageBuilder message = execute3d(pos1, pos2);
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }
        )).executes(ctx -> {
                    BlockPos pos1 = ctx.getSource().getPlayer().getBlockPos();
                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(ctx, "pos1");
                    CalcMessageBuilder message = execute3d(pos1, pos2);
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }
        ))).then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("dist");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static CalcMessageBuilder execute(BlockPos pos1, BlockPos pos2) {
        double dist = Math.sqrt(Math.pow(pos1.getX()-pos2.getX(), 2) + Math.pow(pos1.getZ()-pos2.getZ(), 2));
        CalcMessageBuilder message = new CalcMessageBuilder().addString("Distance from ").addInput("X: "+pos1.getX()+" Z: "+pos1.getZ()).addString(" to ").addInput("X: "+pos2.getX()+" Z: "+pos2.getZ()).addString(": ").addResult(String.valueOf(nf.format(dist)));
        return message;
    }

    public static CalcMessageBuilder execute3d(BlockPos pos1, BlockPos pos2) {
        double dist = Math.sqrt(Math.pow(pos1.getX()-pos2.getX(), 2) + Math.pow(pos1.getY()-pos2.getY(), 2) + Math.pow(pos1.getZ()-pos2.getZ(), 2));
        CalcMessageBuilder message = new CalcMessageBuilder().addString("Distance from ").addInput("X: "+pos1.getX()+" Y: "+pos1.getY()+" Z: "+pos1.getZ()).addString(" to ").addInput("X: "+pos2.getX()+" Y: "+pos2.getY()+" Z: "+pos1.getZ()).addString(": ").addResult(String.valueOf(nf.format(dist)));
        return message;
    }

    public static String helpMessage = """
        §b§LDistance:§r§f
            Given two block positions, gives distance between them. If only one position is given, uses player's location. The 3D mode provides distance including height.
            §eUsage: /calc dist <x1> <y1>§f
            §eUsage: /calc dist <x1> <y1> <z1> <x2> <x2> <z2>§f
            §eUsage: /calc dist 3d <x1> <y1>§f
            §eUsage: /calc dist 3d <x1> <y1> <z1> <x2> <x2> <z2>§f
                """;
}
