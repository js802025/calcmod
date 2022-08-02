package net.jsa2025.calcmod.commands;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;

import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;


import static dev.xpple.clientarguments.arguments.CBlockPosArgumentType.*;

import java.util.Locale;

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US")); 
    public static void register (CommandDispatcher<FabricClientCommandSource> dispacther, CommandRegistryAccess registry) {
        dispacther.register(ClientCommandManager.literal("calc")
        .then(ClientCommandManager.argument("expression", StringArgumentType.greedyString()).executes(ctx -> executeBasicCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "expression"))))
        .then(ClientCommandManager.literal("storage").then(ClientCommandManager.argument("itemsperhour", IntegerArgumentType.integer())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "itemsperhour"), 1))
        .then(ClientCommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed"))))))
        .then(ClientCommandManager.literal("nether").then(ClientCommandManager.argument("pos", blockPos())
        .executes(ctx -> executeNetherCoord(ctx.getSource(), getCBlockPos(ctx, "pos")))))
        .then(ClientCommandManager.literal("overworld").then(ClientCommandManager.argument("pos", blockPos())
        .executes(ctx -> executeOverworldCoord(ctx.getSource(), getCBlockPos(ctx, "pos")))))
        .then(ClientCommandManager.literal("sbtoitem").then(ClientCommandManager.argument("numberofsbs", IntegerArgumentType.integer())
        .executes(ctx -> executeSbToItem(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "numberofsbs")))))
        .then(ClientCommandManager.literal("itemtosb").then(ClientCommandManager.argument("numberofitems", IntegerArgumentType.integer())
        .executes(ctx -> executeItemToSb(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "numberofitems")))))
        .then(ClientCommandManager.literal("SecondstoHopperClock").then(ClientCommandManager.argument("seconds", IntegerArgumentType.integer())
        .executes(ctx -> executeSecondsToHopperClock(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "seconds")))))
        .then(ClientCommandManager.literal("SecondsToRepeater").then(ClientCommandManager.argument("seconds", IntegerArgumentType.integer())
        .executes(ctx -> executeSecondsToRepeater(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "seconds")))))
        .then(ClientCommandManager.literal("itemtostack").then(ClientCommandManager.argument("numberofitems", IntegerArgumentType.integer())
        .executes(ctx -> executeItemToStack(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "numberofitems")))))
        .then(ClientCommandManager.literal("help")
        .executes(ctx -> executeHelp(ctx.getSource())))

        );


    }

    public static int executeBasicCalculation(FabricClientCommandSource source, String expression) {
        Expression e = new Expression(expression);
        String message = "Result: " + nf.format(e.calculate());
        source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));
        return 1;
        
    }

    public static int executeStorageCalculation(FabricClientCommandSource source, int itemsperhour, int timesHopperSpeed) {
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(itemsperhour/hopperSpeed);
        double sbsperhour = itemsperhour * 1.0 / 1728;
        String message = "Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): " + nf.format(sorters) + "\nSbs per hour: " +nf.format(sbsperhour);
        
        source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));

        return 1;
    }

    public static int executeNetherCoord(FabricClientCommandSource source, BlockPos pos ) {
        source.getPlayer().sendMessage(Text.literal("Nether Coord: X: " + nf.format(pos.getX()/8) + " Z: " + nf.format(pos.getZ()/8) + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + nf.format(pos.getX()/8) + " Z: " + nf.format(pos.getZ()/8))))));
        return 1;
    }

    public static int executeOverworldCoord(FabricClientCommandSource source, BlockPos pos ) {
        source.getPlayer().sendMessage(Text.literal("Overworld Coord: X: " + nf.format(pos.getX()*8) + " Z: " + nf.format(pos.getZ()*8) + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + nf.format(pos.getX()*8) + " Z: " + nf.format(pos.getZ()*8))))));
        return 1;
    }

    public static int executeSbToItem(FabricClientCommandSource source, int numberofsbs) {
        double items = numberofsbs * 1728;
        String message = "Items per SB: " + nf.format(items);
        source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));

        return 1;
    }

    public static int executeItemToSb(FabricClientCommandSource source, int numberofitems) {
        double sbs = numberofitems / 1728;
        String message = "Sbs per Item: " + nf.format(sbs);


       source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));
        return 1;
    }

    public static int executeSecondsToHopperClock(FabricClientCommandSource source, int seconds) {
        double hopperclock = Math.ceil(seconds *1.25);
        String message = "Hopper Clock: " + nf.format(hopperclock);
        source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));
        return 1;
    }

    public static int executeSecondsToRepeater(FabricClientCommandSource source, int seconds) {
        int ticks = seconds * 10;
        double repeaters = Math.ceil(ticks/4);
        if (ticks % 4 != 0) {
            String message = "Repeaters Required: " + nf.format(repeaters) +"\nLast Repeater Tick: "+ nf.format(ticks % 4) ;
            source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));

        } else {
            String message = "Repeaters Required: " + nf.format(Math.ceil(ticks/4));
            source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));

        }
        return 1;
    }
    
    public static int executeItemToStack(FabricClientCommandSource source, int items) {
        double stacks = Math.floor(items/64);
        int leftover = items % 64;
        String message = "Stacks Required: " + nf.format(stacks) + "\nLeftover Items: " + nf.format(leftover);
        source.getPlayer().sendMessage(Text.literal(message).append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));
        return 1;
    }

    public static int executeHelp(FabricClientCommandSource source) {
        var helpMessage = """
            Calcmod 

            Basic(no arguments):
            Functions like a simple calculator 
            
            Storage:
            Given rate in terms of items per hour and optionally hopper speed returns the number of needed sorters and rates in terms of sbs per hour
            
            Nether:
            Returns coords in nether for building portal in correct place.
            
            Overworld:
            Same as nether but reversed 
            
            sbtoitem:
            Given number of sbs return number of items
            
            Itemtosb:
            Given number of items return amount of sbs needed
            
            SecondstoHopperClock
            Given seconds return amount of items needed in hopper clock
            2.5 items per second
            
            SecondstoRepeaterClock
            Given number of seconds return needed repeater delay 
                """;;
        source.getPlayer().sendMessage(Text.of(helpMessage));
        return 1;
    }


}