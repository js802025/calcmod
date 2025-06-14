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
        // This will define "sbtoitem <numberofsbs>" (defaulting to stackSize 64)
        // And "sbtoitem help"
        // The "16s" and "1s" variants will be handled as separate commands/aliases in CalcCommand.java
        // or by adding more specific arguments to this "sbtoitem" literal.
        // For now, this focuses on the primary "sbtoitem" name.
        sbToItemLiteral.then(ClientCommandManager.argument("numberofsbs", StringArgumentType.greedyString())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            }))
            .then(ClientCommandManager.literal("help").executes(ctx -> { // Making help a subcommand of sbtoitem
                CalcMessageBuilder message = Help.execute("sbtoitem");
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            }));
        // If sbtoitem itself should be executable (e.g. /calc sbtoitem with no args shows help)
        sbToItemLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("sbtoitem");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> sbToItemLiteral = ClientCommandManager.literal("sbtoitem");
        populateClient(sbToItemLiteral);
        return sbToItemLiteral;
    }

    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> sbToItemLiteral) {
        sbToItemLiteral.then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "numberofsbs"), 64);
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            }))
            .then(CommandManager.literal("help").executes(ctx -> { // Making help a subcommand of sbtoitem
                CalcMessageBuilder message = Help.execute("sbtoitem");
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            }));
        // If sbtoitem itself should be executable
        sbToItemLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("sbtoitem");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> sbToItemLiteral = CommandManager.literal("sbtoitem");
        populateServer(sbToItemLiteral);
        return sbToItemLiteral;
    }

    // Execute method remains the same, stackSize is passed by the command's executes block
    public static CalcMessageBuilder execute(Entity player, String numberofsbs, int stackSize) {
        double sbs = CalcCommand.getParsedExpression(player, numberofsbs, stackSize);
        double items = sbs * stackSize * 27;
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " SBs = ", "result", " Items"}, new String [] {numberofsbs}, new String[] {nf.format(items)});
        return message;
    }

    public static String helpMessage = """
        §b§LSb to Item:§r§f
            Given a number of full Shulker Boxes §7§o(can be in expression form)§r§f, returns the number of items.
            §eUsage: /calc sbtoitem <numberofsbs>§f
                """;
    
}
