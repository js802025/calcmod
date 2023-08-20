package net.jsa2025.calcmod.commands.subcommands;



import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommands;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
public class Help {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
        .then(ClientCommands.literal("help")
        .executes(ctx -> {
            String[] message = execute();
            CalcCommand.sendMessage(ctx.getSource(), message, true);
            return 0;
        }));
        return command;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("help")
        .executes(ctx -> {
            String[] message = execute();
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        }));
        return command;
    }

    public static String[] execute(String... hterm) {
        Map<String, String> help = new LinkedHashMap<String, String>();
        help.put("storage", Storage.helpMessage);
        help.put("nether", Nether.helpMessage);
        help.put("overworld", Overworld.helpMessage);
        help.put("sbtoitem", SbToItem.helpMessage);
        help.put("itemtosb", ItemToSb.helpMessage);
        help.put("secondstohopperclock", SecondsToHopperClock.helpMessage);
        help.put("secondstorepeater", SecondsToRepeater.helpMessage);
        help.put("itemtostack", ItemToStack.helpMessage);
        help.put("stacktoitem", StackToItem.helpMessage);
        help.put("rates", Rates.helpMessage);
        help.put("allaystorage", AllayStorage.helpMessage);
        help.put("craft", Craft.helpMessage);
        help.put("random", Random.helpMessage);
        help.put("signaltoitems", SignalToItems.helpMessage);
        if (hterm.length == 0) {
            String helpMenu = "";
            for (Map.Entry<String, String> me :
             help.entrySet()) {
                helpMenu += me.getValue() + "\n";
  
              }
            String[] message = {helpMenu};
            return message;
        } else {
            String[] message = {help.get(hterm[0])};
            return message;
        }
    }

    
}
