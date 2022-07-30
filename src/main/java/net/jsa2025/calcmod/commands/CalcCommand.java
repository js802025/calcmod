package net.jsa2025.calcmod.commands;

import com.ibm.icu.text.DecimalFormat;
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

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    public static void register (CommandDispatcher<FabricClientCommandSource> dispacther, CommandRegistryAccess registry) {
        dispacther.register(ClientCommandManager.literal("calc").then(ClientCommandManager.literal("basic").then(ClientCommandManager.argument("expression", StringArgumentType.string()).executes(ctx -> executeBasicCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "expression")))))
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

        );


    }

    public static int executeBasicCalculation(FabricClientCommandSource source, String expression) {
        Expression e = new Expression(expression);
        source.getPlayer().sendMessage(Text.of("Result: " + e.calculate()));
        return 1;
        
    }

    public static int executeStorageCalculation(FabricClientCommandSource source, int itemsperhour, int timesHopperSpeed) {
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(itemsperhour/hopperSpeed);
        double sbsperhour = itemsperhour * 1.0 / 1728;

        
        source.getPlayer().sendMessage(Text.of("Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): " + sorters + "\nSbs per hour: " +df.format(sbsperhour)));
        return 1;
    }

    public static int executeNetherCoord(FabricClientCommandSource source, BlockPos pos ) {
        source.getPlayer().sendMessage(Text.literal("Nether Coord: X: " + pos.getX()/8 + " Z: " + pos.getZ()/8 + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + pos.getX()/8 + " Z: " + pos.getZ()/8)))));
        return 1;
    }

    public static int executeOverworldCoord(FabricClientCommandSource source, BlockPos pos ) {
        source.getPlayer().sendMessage(Text.literal("Overworld Coord: X: " + pos.getX()*8 + " Z: " + pos.getZ()*8 +" ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + pos.getX()/8 + " Z: " + pos.getZ()/8)))));
        return 1;
    }

    public static int executeSbToItem(FabricClientCommandSource source, int numberofsbs) {
        double items = numberofsbs * 1728;
        source.getPlayer().sendMessage(Text.of("Items per SB: " + items));
        return 1;
    }

    public static int executeItemToSb(FabricClientCommandSource source, int numberofitems) {
        double sbs = numberofitems / 1728;
        source.getPlayer().sendMessage(Text.of("Sbs per Item: " + df.format(sbs)));
        return 1;
    }

    public static int executeSecondsToHopperClock(FabricClientCommandSource source, int seconds) {
        double hopperclock = Math.ceil(seconds *2.5);
        source.getPlayer().sendMessage(Text.of("Items Required: " + hopperclock));
        return 1;
    }

    public static int executeSecondsToRepeater(FabricClientCommandSource source, int seconds) {
        int ticks = seconds * 10;
        double repeaters = Math.ceil(ticks/4);
        if (ticks % 4 != 0) {
            source.getPlayer().sendMessage(Text.of("Repeaters Required: " + repeaters +"\nLast Repeater Tick: "+ticks % 4 ));
        } else {
            source.getPlayer().sendMessage(Text.of("Repeaters Required: " + Math.ceil(ticks/4)));
        }
        return 1;
    }


}