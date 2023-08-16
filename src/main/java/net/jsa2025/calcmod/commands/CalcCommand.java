package net.jsa2025.calcmod.commands;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.jsa2025.calcmod.commands.subcommands.Basic;
import net.jsa2025.calcmod.commands.subcommands.Craft;
import  net.jsa2025.calcmod.commands.subcommands.Storage;
import net.jsa2025.calcmod.commands.subcommands.Nether;
import net.jsa2025.calcmod.commands.subcommands.Overworld;
import net.jsa2025.calcmod.commands.subcommands.Random;
import net.jsa2025.calcmod.commands.subcommands.SbToItem;
import net.jsa2025.calcmod.commands.subcommands.ItemToSb;
import net.jsa2025.calcmod.commands.subcommands.SecondsToHopperClock;
import net.jsa2025.calcmod.commands.subcommands.SecondsToRepeater;
import net.jsa2025.calcmod.commands.subcommands.SignalToItems;
import net.jsa2025.calcmod.commands.subcommands.ItemToStack;
import net.jsa2025.calcmod.commands.subcommands.StackToItem;
import net.jsa2025.calcmod.commands.subcommands.Rates;
import net.jsa2025.calcmod.commands.subcommands.AllayStorage;
import net.jsa2025.calcmod.commands.subcommands.Help;
import net.jsa2025.calcmod.commands.subcommands.Variables;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;


import java.util.HashMap;
import java.util.Locale;

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    public static void register (CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registry) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("calc");
        command = Basic.register(command);
        command = Storage.register(command);
        command = Nether.register(command);
        command = Overworld.register(command);
        command = SbToItem.register(command);
        command = ItemToSb.register(command);
        command = SecondsToHopperClock.register(command);
        command = SecondsToRepeater.register(command);
        command = ItemToStack.register(command);
        command = StackToItem.register(command);
        command = Rates.register(command);
        command = AllayStorage.register(command);
        command = Random.register(command);
        command = Craft.register(command, registry);
        command = SignalToItems.register(command);
        command = Variables.register(command);
        command = Help.register(command);
        dispatcher.register(command);

    }

    public static void registerServer(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, RegistrationEnvironment env) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("calc");
        command = Basic.registerServer(command);
        command = Storage.registerServer(command);
        command = Nether.registerServer(command);
        command = Overworld.registerServer(command);
        command = SbToItem.registerServer(command);
        command = ItemToSb.registerServer(command);
        command = SecondsToHopperClock.registerServer(command);
        command = SecondsToRepeater.registerServer(command);
        command = ItemToStack.registerServer(command);
        command = StackToItem.registerServer(command);
        command = Rates.registerServer(command);
        command = AllayStorage.registerServer(command);
        command = Random.registerServer(command);
        command = Craft.registerServer(command, registry);
        command = SignalToItems.registerServer(command);
        command = Variables.registerServer(command);
        command = Help.registerServer(command);

        dispatcher.register(command);
    }

   

    public static double getParsedExpression(BlockPos playerPos, String in,Integer... nonstackable) {
        int stackSize;
        if (nonstackable.length == 0) stackSize = 64;
        else stackSize = nonstackable[0];
        HashMap<String, Double> vars = new HashMap<>();
        vars.put("dub64", 3456.0);
        vars.put("dub16", 864.0);
        vars.put("dub1", 54.0);
        vars.put("sb64", 1728.0);
        vars.put("sb16", 432.0);
        vars.put("sb1", 27.0);
        vars.put("stack64", 64.0);
        vars.put("stack16", 16.0);
        vars.put("stack1", 1.0);
        vars.put("min", 60.0);
        vars.put("hour", 3600.0);
        vars.put("x", (double)playerPos.getX());
        vars.put("y", (double) playerPos.getX());
        vars.put("z", (double) playerPos.getZ());
        vars.put("dub", vars.get("dub"+ stackSize));
        vars.put("sb", vars.get("sb"+stackSize));
        vars.put("stack", vars.get("stack"+stackSize));
        String withVars = in;

        for (String key : vars.keySet()) {
            withVars = withVars.replaceAll(key, "("+vars.get(key)+")");
        }
//        if (nonstackable.length > 0) {
//            if (nonstackable[0] == 1) {
//            return new Expression(in.replaceAll("dub64", "(3456)").replaceAll("dub16", "(864)").replaceAll("dub1", "(54)").replaceAll("sb64", "(1728)").replaceAll("sb16", "(432)").replaceAll("sb1", "(27)").replaceAll("stack64", "(64)").replaceAll("stack16", "(16)").replaceAll("stack1", "(1)").replaceAll("dub", "(54)").replaceAll("sb", "(27)").replaceAll("stack", "(1)").replaceAll("min", "(60)").replaceAll("hour", "(3600)").replaceAll("x", "("+String.valueOf(playerPos.getX())+")").replaceAll("y", "("+String.valueOf(playerPos.getY())+")").replaceAll("z", "("+String.valueOf(playerPos.getZ())+")").replaceAll(",", "")).calculate();
//            } else if (nonstackable[0] == 16) {
//               return  new Expression(in.replaceAll("dub64", "(3456)").replaceAll("dub16", "(864)").replaceAll("dub1", "(54)").replaceAll("sb64", "(1728)").replaceAll("sb16", "(432)").replaceAll("sb1", "(27)").replaceAll("stack64", "(64)").replaceAll("stack16", "(16)").replaceAll("stack1", "(1)").replaceAll("dub", "(864)").replaceAll("sb", "(432)").replaceAll("stack", "(16)").replaceAll("min", "(60)").replaceAll("hour", "(3600)").replaceAll("x", "("+String.valueOf(playerPos.getX())+")").replaceAll("y", "("+String.valueOf(playerPos.getY())+")").replaceAll("z", "("+String.valueOf(playerPos.getZ())+")").replaceAll(",", "")).calculate();
//            }
//        }
            return new Expression(withVars).calculate();
        }

    public static String getParsedStack(double items, int stacksize) {
        if (items >= 64) {
            return "Stacks: "+nf.format(Math.floor(items / stacksize))+", Items: "+ nf.format(items % stacksize);
        } else {
            return nf.format(items);
        }
    }

    public static void sendMessage(FabricClientCommandSource source, String[] message, Boolean... isHelpMessage) {
        var messageText = Text.literal("");
        String m = "";
        for (var i = 0; i < message.length; i++) {
           if (i % 2 == 0) {
            messageText.append(Text.literal(message[i]));
            m += message[i];
           } else {
            messageText.append(Text.literal("§a"+message[i]+"§f").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message[i]))));
            m += message[i];
           }
           
        }
        
        if (isHelpMessage.length > 0) {
            if (isHelpMessage[0]) {
                source.getPlayer().sendMessage(messageText);
                return;
            } 
        }
        messageText.append(Text.literal(" "));
        source.getPlayer().sendMessage(messageText.append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, m.replaceAll("§a", "").replaceAll("§f", ""))))));
    }

    public static void sendMessageServer(ServerCommandSource source, String[] message, Boolean... isHelpMessage) {
        var messageText = Text.literal("");
        String m = "";
        for (var i = 0; i < message.length; i++) {
           if (i % 2 == 0) {
            messageText.append(Text.literal(message[i]));
            m += message[i];
           } else {
            messageText.append(Text.literal("§a"+message[i]+"§f").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message[i]))));
            m += message[i];
           }
           
        }

        if (isHelpMessage.length > 0) {
            if (isHelpMessage[0]) {
                source.getPlayer().sendMessage(messageText);
                return;
            } 
        }
        messageText.append(Text.literal(" "));
        source.getPlayer().sendMessage(messageText.append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, m.replaceAll("§a", "").replaceAll("§f", ""))))));
    }

    


}