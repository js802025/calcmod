package net.jsa2025.calcmod.commands;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.jsa2025.calcmod.commands.subcommands.Basic;
import  net.jsa2025.calcmod.commands.subcommands.Storage;
import net.jsa2025.calcmod.commands.subcommands.Nether;
import net.jsa2025.calcmod.commands.subcommands.Overworld;
import net.jsa2025.calcmod.commands.subcommands.Random;
import net.jsa2025.calcmod.commands.subcommands.SbToItem;
import net.jsa2025.calcmod.commands.subcommands.ItemToSb;
import net.jsa2025.calcmod.commands.subcommands.SecondsToHopperClock;
import net.jsa2025.calcmod.commands.subcommands.SecondsToRepeater;
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
import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;


import java.util.Locale;

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US")); 
    public static void register (CommandDispatcher<FabricClientCommandSource> dispacther, CommandRegistryAccess registry) {
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
        command = Variables.register(command);
        command = Help.register(command);
        dispacther.register(command);

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
        command = Variables.registerServer(command);
        command = Help.registerServer(command);
        dispatcher.register(command);
    }

   

    public static double getParsedExpression(String in, Integer... nonstackable) {
        if (nonstackable.length > 0) {
            if (nonstackable[0] == 1) {
            return new Expression(in.replaceAll("dub64", "(3456)").replaceAll("dub16", "(864)").replaceAll("dub1", "(54)").replaceAll("sb64", "(1728)").replaceAll("sb16", "(432)").replaceAll("sb1", "(27)").replaceAll("stack64", "(64)").replaceAll("stack16", "(16)").replaceAll("stack1", "(1)").replaceAll("dub", "(54)").replaceAll("sb", "(27)").replaceAll("stack", "(1)").replaceAll("min", "(60)").replaceAll("hour", "(3600)").replaceAll(",", "")).calculate();
            } else if (nonstackable[0] == 16) {
               return  new Expression(in.replaceAll("dub64", "(3456)").replaceAll("dub16", "(864)").replaceAll("dub1", "(54)").replaceAll("sb64", "(1728)").replaceAll("sb16", "(432)").replaceAll("sb1", "(27)").replaceAll("stack64", "(64)").replaceAll("stack16", "(16)").replaceAll("stack1", "(1)").replaceAll("dub", "(864)").replaceAll("sb", "(432)").replaceAll("stack", "(16)").replaceAll("min", "(60)").replaceAll("hour", "(3600)").replaceAll(",", "")).calculate();
            }
        }
            return new Expression(in.replaceAll("dub64", "(3456)").replaceAll("dub16", "(864)").replaceAll("dub1", "(54)").replaceAll("sb64", "(1728)").replaceAll("sb16", "(432)").replaceAll("sb1", "(27)").replaceAll("stack64", "(64)").replaceAll("stack16", "(16)").replaceAll("stack1", "(1)").replaceAll("dub", "(3456)").replaceAll("sb", "(1728)").replaceAll("stack", "(64)").replaceAll("min", "(60)").replaceAll("hour", "(3600)").replaceAll(",", "")).calculate();
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