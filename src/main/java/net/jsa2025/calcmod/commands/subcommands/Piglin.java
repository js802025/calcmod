package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.CBarterSuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;


public class Piglin {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
                .then(ClientCommandManager.literal("piglin")
                        .then(ClientCommandManager.argument("gold", IntegerArgumentType.integer())
                                .then(ClientCommandManager.argument("item", StringArgumentType.string()).suggests(new CBarterSuggestionProvider())
                        .executes((ctx) -> {
                            Integer gold = IntegerArgumentType.getInteger(ctx, "gold");
                            String item = StringArgumentType.getString(ctx, "item");
                            String[] message = execute(ctx.getSource().getPlayer(), gold, item);
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        }))).then(ClientCommandManager.literal("help").executes((ctx) -> {
                    String[] message = Help.execute("nether");
                    CalcCommand.sendMessage(ctx.getSource(), message, true);
                    return 1;
                })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//                .then(CommandManager.literal("nether").executes((ctx) -> {
//                    String[] message = execute(ctx.getSource().getPlayer().getBlockPos());
//                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                    return 1;
//                }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
//                        .executes((ctx) -> {
//                            BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");
//                            String[] message = execute(pos);
//                            CalcCommand.sendMessageServer(ctx.getSource(), message);
//                            return 1;
//                        })).then(CommandManager.literal("help").executes((ctx) -> {
//                    String[] message = Help.execute("nether");
//                    CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//                    return 1;
//                })));
        return command;
    }

    public static String[] execute(PlayerEntity player, Integer gold, String item) {
        double amount_of_items = gold/CBarterSuggestionProvider.barter.get(item);
        String[] message = {"Number of "+item+"s that "+gold+" gold will get: \nResult: ", String.valueOf(amount_of_items)};
        return message;


    }

    public static String helpMessage = """
        §LNether:§r
            Given a block position in the overworld, returns the nether coordinates
            §cUsage: /calc nether <x> <y> <z>§f
                """;
}
