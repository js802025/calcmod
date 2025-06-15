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

public class ItemToSb {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> itemToSbLiteral) {
        itemToSbLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        itemToSbLiteral.then(ClientCommandManager.literal("help").executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        itemToSbLiteral.then(ClientCommandManager.literal("convert")
            .then(ClientCommandManager.argument("numberofitems", StringArgumentType.string())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
        
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> itemToSbLiteral = ClientCommandManager.literal("itemtosb");
        populateClient(itemToSbLiteral);
        return itemToSbLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> itemToSbLiteral) {
        itemToSbLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        itemToSbLiteral.then(CommandManager.literal("help").executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("itemtosb");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        itemToSbLiteral.then(CommandManager.literal("convert")
            .then(CommandManager.argument("numberofitems", StringArgumentType.string())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofitems"), 64);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
        
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> itemToSbLiteral = CommandManager.literal("itemtosb");
        populateServer(itemToSbLiteral);
        return itemToSbLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofitems, int stackSize) {
        double items = CalcCommand.getParsedExpression(player, numberofitems, stackSize);
        double sbs = items / (stackSize * 27.0); 
        CalcMessageBuilder message= new CalcMessageBuilder().addInput(numberofitems).addString(" Items = ").addResult(df.format(sbs)).addString(" SBs");

        return message;
    }

    public static String helpMessage = """
        §b§LItem to Sb:§r§f
        Converts a given number of items to the equivalent number of Shulker Boxes.
        Base command §e/calc itemtosb§r or §e/calc itemtosb help§r shows this message.
        
        §eUsage: /calc itemtosb convert <numberofitems>§f
          <numberofitems>: Number of items (can be an expression).
          Example: /calc itemtosb convert 2000
                """;
}
