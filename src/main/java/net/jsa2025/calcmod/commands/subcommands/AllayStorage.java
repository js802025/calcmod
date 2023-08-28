package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommandManager.literal("allaystorage").then(ClientCommandManager.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "itemsperhour"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(ClientCommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("allaystorage").then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "itemsperhour"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(CommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }
    

    public static CalcMessageBuilder execute(PlayerEntity player, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(player.getBlockPos(), itemsperhour, 1);
        double ratesinsec = rates / 3600;
        double allaycooldown = 3;
        String allaystorage = nf.format(Math.ceil(ratesinsec/(1/allaycooldown)));

        return new CalcMessageBuilder().addString("Allays Needed to Process ").addInput(itemsperhour).addString(" items: ").addResult(String.valueOf(allaystorage));
    }

    public static String helpMessage = """
        §b§LAllay Storage:§r§f
            Given the number of items per hour of a non stackable item §7§o(can be in expression form)§r§f, returns allays needed to sort the item.
            §eUsage: /calc allaystorage <numberofitems>§f
            """;


}
