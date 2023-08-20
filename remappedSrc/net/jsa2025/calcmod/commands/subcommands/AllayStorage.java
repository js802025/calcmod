package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommands;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("allaystorage").then(ClientCommands.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "itemsperhour"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 0;
        })).then(ClientCommands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("allaystorage");
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("allaystorage").then(Commands.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "itemsperhour"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })).then(Commands.literal("help").executes((ctx) -> {
            String[] message = Help.execute("allaystorage");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }
    

    public static String[] execute(ServerPlayerEntity player, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(player.getEntity().blockPosition(), itemsperhour, 1);
        double ratesinsec = rates / 3600;
        double allaycooldown = 3;
        String allaystorage = nf.format(Math.ceil(ratesinsec/(1/allaycooldown)));

        String[] message = {"Allays Needed: ", allaystorage};
        return message;
    }

    public static String helpMessage = """
        §LAllay Storage:§r
            Given the number of items per hour of a non stackable item, returns allays needed to sort the item.
            §cUsage: /calc allaystorage <numberofitems>§f
            """;


}
