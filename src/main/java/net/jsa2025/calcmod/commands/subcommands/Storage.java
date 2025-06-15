package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class Storage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> storageLiteral) {
        storageLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        storageLiteral.then(ClientCommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        storageLiteral.then(ClientCommandManager.literal("items")
            .then(ClientCommandManager.argument("itemsperhour", StringArgumentType.string())
                .executes((ctx) -> {
                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                })));
        
        storageLiteral.then(ClientCommandManager.literal("customspeed")
            .then(ClientCommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
                .then(ClientCommandManager.argument("itemsperhour_for_speed", StringArgumentType.string()) 
                    .executes((ctx) -> { 
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour_for_speed"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
                        CalcCommand.sendMessage(ctx.getSource(), message);
                        return 1;
                    }))));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> storageLiteral = ClientCommandManager.literal("storage");
        populateClient(storageLiteral);
        return storageLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> storageLiteral) {
        storageLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        storageLiteral.then(CommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        storageLiteral.then(CommandManager.literal("items")
            .then(CommandManager.argument("itemsperhour", StringArgumentType.string())
                .executes((ctx) -> {
                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                })));
        
        storageLiteral.then(CommandManager.literal("customspeed")
            .then(CommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
                .then(CommandManager.argument("itemsperhour_for_speed", StringArgumentType.string()) 
                    .executes((ctx) -> { 
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour_for_speed"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
                        CalcCommand.sendMessageServer(ctx.getSource(), message);
                        return 1;
                    }))));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> storageLiteral = CommandManager.literal("storage");
        populateServer(storageLiteral);
        return storageLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour);
        double hopperSpeed = (9000.0 * timesHopperSpeed); 
        if (hopperSpeed == 0) { 
            return new CalcMessageBuilder().addString("Error: Hopper speed cannot be zero.");
        }
        double sorters = Math.ceil(rates / hopperSpeed);
        double sbsperhour = rates / 1728.0; 

        return new CalcMessageBuilder().addFromArray(
            new String[] {"Required ","input","xHopper speed §7(9,000/hr)§f sorters for ", "input"," items/hr = ", "result", " \nSBs/hr = ", "result"},
            new String[] {nf.format(timesHopperSpeed), itemsperhour}, 
            new String[] {nf.format(sorters), df.format(sbsperhour)} 
        );
    }

    public static String helpMessage = """
        §b§LStorage:§r§f
        Calculates the number of item sorters needed for a given item rate.
        Base command §e/calc storage§r or §e/calc storage help§r shows this message.
        
        §eUsage: /calc storage items <itemsperhour>§f
          Calculates sorters for items at 1x hopper speed (9,000 items/hr).
          Example: /calc storage items 10000
          
        §eUsage: /calc storage customspeed <timesHopperSpeed> <itemsperhour>§f
          Calculates sorters for items at a custom hopper speed multiplier.
          <timesHopperSpeed>: Multiplier for hopper speed (e.g., 2 for 18,000 items/hr).
          <itemsperhour>: The rate of items to be sorted.
          Example: /calc storage customspeed 2 20000
                """;
}
