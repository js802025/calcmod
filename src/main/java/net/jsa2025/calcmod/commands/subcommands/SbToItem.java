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


public class SbToItem {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> sbToItemLiteral) {
        sbToItemLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.executeSpecificHelp("sbtoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        sbToItemLiteral.then(ClientCommandManager.literal("help").executes(ctx -> { 
            CalcMessageBuilder message = Help.executeSpecificHelp("sbtoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));

        sbToItemLiteral.then(ClientCommandManager.literal("convert")
            .then(ClientCommandManager.argument("numberofsbs", StringArgumentType.string())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> sbToItemLiteral = ClientCommandManager.literal("sbtoitem");
        populateClient(sbToItemLiteral);
        return sbToItemLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> sbToItemLiteral) {
        sbToItemLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.executeSpecificHelp("sbtoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        sbToItemLiteral.then(CommandManager.literal("help").executes(ctx -> { 
            CalcMessageBuilder message = Help.executeSpecificHelp("sbtoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        sbToItemLiteral.then(CommandManager.literal("convert")
            .then(CommandManager.argument("numberofsbs", StringArgumentType.string())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> sbToItemLiteral = CommandManager.literal("sbtoitem");
        populateServer(sbToItemLiteral);
        return sbToItemLiteral;
    }

    public static CalcMessageBuilder execute(Entity player, String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(player, numberofsbs, stackSize);
        double items = sbs * stackSize * 27;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " SBs = ", "result", " Items"}, new String [] {numberofsbs}, new String[] {nf.format(items)});
        return message;
    }

    public static String helpMessage = """
        §b§LSb to Item:§r§f
        Converts a given number of Shulker Boxes to the equivalent number of items.
        Base command §e/calc sbtoitem§r or §e/calc sbtoitem help§r shows this message.
        
        §eUsage: /calc sbtoitem convert <numberofsbs>§f
          <numberofsbs>: Number of Shulker Boxes (can be an expression).
          Example: /calc sbtoitem convert 2.5
                """;
    
}
