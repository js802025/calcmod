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

import org.bukkit.entity.Entity;

public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("overworld").executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), ctx.getSource().getLocation().toBlock());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }).then(Commands.argument("pos", ArgumentTypes.blockPosition())
        .executes((ctx) -> {
            BlockPosition pos = ctx.getArgument("pos", BlockPositionResolver.class).resolve(ctx.getSource());
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), pos);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("overworld");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("overworld").executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), ctx.getSource().getEntity().getBlockPos());
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
//        .executes((ctx) -> {
//            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), pos);
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })).then(CommandManager.literal("help").executes((ctx) -> {
//            CalcMessageBuilder message = Help.execute("overworld");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, BlockPosition position) {


        CalcMessageBuilder message = new CalcMessageBuilder().addInput("X: "+nf.format(position.toVector().getBlockX())+" Z: "+nf.format(position.toVector().getBlockZ())).addString(" >><white> Overworld = ").addResult("X: "+nf.format(position.toVector().getBlockX()*8)+" Z: "+nf.format(position.toVector().getBlockZ()*8));
        return message;
    }

    public static String helpMessage = """
            <aqua><bold>Overworld:<reset><white>
                Given a block position in the Nether, returns the Overworld's corresponding coordinates. If no coordinates are given, command assumes current player position.
                <yellow>Usage: /calc overworld <x> <y> <z><white>
                    """;


}
