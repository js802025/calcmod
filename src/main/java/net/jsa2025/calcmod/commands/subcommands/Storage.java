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
        // Path 1: /calc storage help (Literal, most specific)
        storageLiteral.then(ClientCommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        // Path 2: /calc storage <timesHopperSpeed_int> [itemsperhour_greedyString] (Specific typed argument)
        storageLiteral.then(ClientCommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
            .executes((ctx) -> { // Handles /calc storage <timesHopperSpeed>
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })
            .then(ClientCommandManager.argument("itemsperhour_for_speed", StringArgumentType.greedyString()) // Unique name for this amount
            .executes((ctx) -> { // Handles /calc storage <timesHopperSpeed> <itemsperhour>
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour_for_speed"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
        
        // Path 3: /calc storage <itemsperhour_greedyString> (Greedy string, most general)
        // This argument name "itemsperhour" should be distinct if there's any ambiguity with other paths,
        // but since it's the most general path and defined last among siblings, it should be okay.
        storageLiteral.then(ClientCommandManager.argument("itemsperhour", StringArgumentType.greedyString())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            }));

        // Base command /calc storage shows help
        storageLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> storageLiteral = ClientCommandManager.literal("storage");
        populateClient(storageLiteral);
        return storageLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> storageLiteral) {
        // Path 1: /calc storage help (Literal, most specific)
        storageLiteral.then(CommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        // Path 2: /calc storage <timesHopperSpeed_int> [itemsperhour_greedyString] (Specific typed argument)
        storageLiteral.then(CommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
            .executes((ctx) -> { // Handles /calc storage <timesHopperSpeed>
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })
            .then(CommandManager.argument("itemsperhour_for_speed", StringArgumentType.greedyString()) // Unique name
            .executes((ctx) -> { // Handles /calc storage <timesHopperSpeed> <itemsperhour>
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour_for_speed"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"));
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
        
        // Path 3: /calc storage <itemsperhour_greedyString> (Greedy string, most general)
        storageLiteral.then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"), 1);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            }));
        
        // Base command /calc storage shows help
        storageLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("storage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> storageLiteral = CommandManager.literal("storage");
        populateServer(storageLiteral);
        return storageLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String itemsperhour, int timesHopperSpeed) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour);
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"Required ","input","xHopper speed §7(9,000/hr)§f sorters for ", "input"," items/hr = ", "result", " \nSBs/hr = ", "result"}, new String[] {nf.format(timesHopperSpeed), itemsperhour}, new String[] {nf.format(sorters), nf.format(sbsperhour)});
        
        return message;
    }

    public static String helpMessage = """
        §b§LStorage:§r§f
        Calculates the number of needed item sorters given a rate of items per hour §7§o(can be in expression form)§r§f. Additional input for multiple times hopper speed sorters.
                §eUsage: /calc storage <itemsperhour>
                Usage: /calc storage <timesHopperSpeed> <itemsperhour>§f
                """;
}
