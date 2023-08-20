package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommands;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;
//import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), ctx.getSource().getPlayer().getBlockPos());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        }).then(ClientCommands.argument("pos", CBlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = CBlockPosArgumentType.getCBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getPlayer(), pos);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })).then(ClientCommands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("overworld");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("overworld").executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayer(), ctx.getSource().getPlayer().getBlockPos());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }).then(Commands.argument("pos", BlockPosArgumentType.blockPos())
        .executes((ctx) -> {
            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
            String[] message = execute(ctx.getSource().getPlayer(), pos);
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
