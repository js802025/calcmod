package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType; 

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent; 
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text; 

public class Help {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    private static final Map<String, String> helpMessages = new LinkedHashMap<>();
    static {
        helpMessages.put("storage", Storage.helpMessage);
        helpMessages.put("craft", Craft.helpMessage);
        helpMessages.put("random", Random.helpMessage);
        helpMessages.put("rates", Rates.helpMessage);
        helpMessages.put("sbtoitem", SbToItem.helpMessage);
        helpMessages.put("itemtosb", ItemToSb.helpMessage);
        helpMessages.put("itemtostack", ItemToStack.helpMessage);
        helpMessages.put("stacktoitem", StackToItem.helpMessage);
        helpMessages.put("secondstohopperclock", SecondsToHopperClock.helpMessage);
        helpMessages.put("secondstorepeater", SecondsToRepeater.helpMessage);
        helpMessages.put("allaystorage", AllayStorage.helpMessage);
        helpMessages.put("signaltoitems", SignalToItems.helpMessage);
        helpMessages.put("nether", Nether.helpMessage);
        helpMessages.put("overworld", Overworld.helpMessage);
        helpMessages.put("barter", Piglin.helpMessage);
        helpMessages.put("custom", Custom.helpMessage);
    }
    public static final int ENTRIES_PER_PAGE = 4; 

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> helpLiteral = ClientCommandManager.literal("help");
        helpLiteral
            .executes(ctx -> {
                CalcMessageBuilder message = execute(1); 
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })
            .then(ClientCommandManager.argument("page", IntegerArgumentType.integer(1)) 
                .executes(ctx -> {
                    int page = IntegerArgumentType.getInteger(ctx, "page");
                    CalcMessageBuilder message = execute(page);
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }));
        return helpLiteral;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> helpLiteral = CommandManager.literal("help");
        helpLiteral
            .executes(ctx -> {
                CalcMessageBuilder message = execute(1); 
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })
            .then(CommandManager.argument("page", IntegerArgumentType.integer(1)) 
                .executes(ctx -> {
                    int page = IntegerArgumentType.getInteger(ctx, "page");
                    CalcMessageBuilder message = execute(page);
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }));
        return helpLiteral;
    }

    public static CalcMessageBuilder executeSpecificHelp(String hterm) {
        String helpText = helpMessages.get(hterm.toLowerCase(Locale.ROOT));
        if (helpText != null) {
            return new CalcMessageBuilder(helpText);
        } else {
            CalcMessageBuilder specificNotFound = new CalcMessageBuilder();
            specificNotFound.addString("No specific help available for: " + hterm + "\n");
            return specificNotFound.concat(getPaginatedHelp(1));
        }
    }

    public static CalcMessageBuilder execute(int pageNumber) {
        return getPaginatedHelp(pageNumber);
    }
    
    private static CalcMessageBuilder getPaginatedHelp(int pageNumber) {
        CalcMessageBuilder message = new CalcMessageBuilder(); 
        List<Map.Entry<String, String>> entries = new ArrayList<>(helpMessages.entrySet());

        int totalEntries = entries.size();
        if (totalEntries == 0) {
            message.addString("§cNo help entries available.§r");
            return message;
        }

        int totalPages = (int) Math.ceil((double) totalEntries / ENTRIES_PER_PAGE);
        pageNumber = Math.max(1, Math.min(pageNumber, totalPages)); 

        message.addString("§6--- Calc Mod Help (Page " + pageNumber + "/" + totalPages + ") ---§r\n");
        message.addString("Use §e/calc help <page>§r to view other pages.\n");
        message.addString("Click command for specific help (e.g. §e/calc <command> help§r).\n\n");
        
        int startIndex = (pageNumber - 1) * ENTRIES_PER_PAGE;
        int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, totalEntries);

        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<String, String> entry = entries.get(i);
            String commandName = entry.getKey();
            String helpText = entry.getValue();
            String firstLineOfHelp = helpText.split("\n")[0].replace("§b§L", "").replace("§r§f", "").trim(); 

            MutableText clickableCommandName = Text.literal("§e/calc " + commandName + "§r");
            clickableCommandName.setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/calc " + commandName + " help"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Suggest: /calc " + commandName + " help"))));
            
            message.appendText(clickableCommandName); 
            message.addString(": " + firstLineOfHelp + "\n");
        }

        if (totalPages > 1) {
            message.addString("\n"); 
            MutableText footer = Text.literal("");
            Style prevStyle = Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/calc help " + (pageNumber - 1)))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Go to page " + (pageNumber-1))));
            Style nextStyle = Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/calc help " + (pageNumber + 1)))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Go to page " + (pageNumber+1))));

            if (pageNumber > 1) {
                footer.append(Text.literal("§b[« Previous]").setStyle(prevStyle));
            }
            if (pageNumber > 1 && pageNumber < totalPages) {
                footer.append(Text.literal("   ")); 
            }
            if (pageNumber < totalPages) {
                footer.append(Text.literal("§b[Next »]").setStyle(nextStyle));
            }
            if (!footer.getString().isEmpty()) { 
                message.appendText(footer); 
            }
        }
        return message;
    }
}
