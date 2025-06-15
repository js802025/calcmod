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

public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##"); 
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static void populateClient(LiteralArgumentBuilder<FabricClientCommandSource> allayStorageLiteral) {
        allayStorageLiteral.executes(ctx -> { // Base command shows help
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });
        
        allayStorageLiteral.then(ClientCommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        }));
        
        allayStorageLiteral.then(ClientCommandManager.literal("calculate")
            .then(ClientCommandManager.argument("itemsperhour", StringArgumentType.string())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"));
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> allayStorageLiteral = ClientCommandManager.literal("allaystorage");
        populateClient(allayStorageLiteral);
        return allayStorageLiteral;
    }
    
    private static void populateServer(LiteralArgumentBuilder<ServerCommandSource> allayStorageLiteral) {
        allayStorageLiteral.executes(ctx -> { 
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        allayStorageLiteral.then(CommandManager.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        allayStorageLiteral.then(CommandManager.literal("calculate")
            .then(CommandManager.argument("itemsperhour", StringArgumentType.string())
            .executes((ctx) -> {
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"));
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> allayStorageLiteral = CommandManager.literal("allaystorage");
        populateServer(allayStorageLiteral);
        return allayStorageLiteral;
    }
    

    public static CalcMessageBuilder execute(Entity player, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour, 1);
        if (rates < 0) {
            return new CalcMessageBuilder().addString("Error: Items per hour must be a non-negative value.");
        }
        if (rates == 0) {
             return new CalcMessageBuilder().addString("Allays needed to sort 0 items/hr = 0");
        }
        double ratesinsec = rates / 3600.0; 
        double allaycooldown = 3.0; 
        double allaysNeeded = Math.ceil(ratesinsec * allaycooldown); 
        String allaystorage = nf.format(allaysNeeded);

        return new CalcMessageBuilder().addString("Allays needed to sort ").addInput(itemsperhour).addString(" items/hr = ").addResult(allaystorage);
    }

    public static String helpMessage = """
        §b§LAllay Storage:§r§f
        Calculates the number of Allays needed to sort non-stackable items at a given rate.
        Assumes each Allay picks up 1 item every 3 seconds.
        Base command §e/calc allaystorage§r or §e/calc allaystorage help§r shows this message.
        
        §eUsage: /calc allaystorage calculate <itemsperhour>§f
          <itemsperhour>: Rate of non-stackable items (can be an expression, non-negative).
          Example: /calc allaystorage calculate 1200
            """;


}
