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
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ItemToStack {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> itemToStackLiteral) {
        itemToStackLiteral.then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        itemToStackLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> itemToStackLiteral = ClientCommandManager.literal("itemtostack");
        populateClient(itemToStackLiteral);
        return itemToStackLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> itemToStackLiteral) {
        itemToStackLiteral.then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }))
        .then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));
        itemToStackLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("itemtostack");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> itemToStackLiteral = CommandManager.literal("itemtostack");
        populateServer(itemToStackLiteral);
        return itemToStackLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player, numberofitems, stackSize);
        double stacks = Math.floor(items/stackSize);
        double leftover = items % stackSize;
        CalcMessageBuilder message = new CalcMessageBuilder().addInput(numberofitems).addString(" ").addInput(nf.format(stackSize)).addString(" Stackable items = ").addResult(nf.format(stacks)).addString(" Stacks + ").addResult(nf.format(leftover)).addString(" Items");
        
        return message;
    }

    public static String helpMessage = """
        §b§LItem to Stack:§r§f
            Given a number of items §7§o(can be in expression form)§r§f, returns the number of stacks and remainder items.
            §eUsage: /calc itemtostack <numberofitems>§f
                """;
}
