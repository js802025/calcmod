package net.jsa2025.calcmod.commands;

import java.text.DecimalFormat;
import java.text.NumberFormat;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;


import com.mojang.brigadier.exceptions.CommandSyntaxException;


import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.subcommands.*;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;


import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Level;
import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.mariuszgromada.math.mxparser.License;
import net.jsa2025.calcmod.commands.subcommands.Random;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.PrimitiveElement;


import java.util.*;
import java.util.function.Supplier;

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
//    public static LiteralArgumentBuilder<FabricClientCommandSource> register () {
//        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommands.literal("calc");
//        command = Basic.register(command);
//        command = Storage.register(command);
//        command = Nether.register(command);
//        command = Overworld.register(command);
//        command = SbToItem.register(command);
//        command = ItemToSb.register(command);
//        command = SecondsToHopperClock.register(command);
//        command = SecondsToRepeater.register(command);
//        command = ItemToStack.register(command);
//        command = StackToItem.register(command);
//        command = Rates.register(command);
//        command = AllayStorage.register(command);
//        command = Random.register(command);
//        command = Craft.register(command);
//        command = SignalToItems.register(command);
//        command = Piglin.register(command);
//        command = Variables.register(command);
//        command = Help.register(command);
//        return command;
//
//    }

    public static void registerServer(CommandDispatcher<CommandSource> dispatcher, boolean dedicated) {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("calc");
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
        command = Craft.registerServer(command);
        command = SignalToItems.registerServer(command);
        command = Piglin.registerServer(command);
        command = Custom.registerServer(command);
        command = Variables.registerServer(command);
        command = Help.registerServer(command);


        dispatcher.register(command);
    }

   

    public static double getParsedExpression(Entity player, String in, Integer... nonstackable) {
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
        if (Objects.nonNull(player)) {
            vars.put("x", (double) player.blockPosition().getX());
            vars.put("y", (double) player.blockPosition().getX());
            vars.put("z", (double) player.blockPosition().getZ());
            //health missing
        }
       //
        vars.put("dub", vars.get("dub"+ stackSize));
        vars.put("sb", vars.get("sb"+stackSize));
        vars.put("stack", vars.get("stack"+stackSize));
        String withVars = in;
        ArrayList<String> parsedCustomFunctions = Custom.getParsedFunctions();
        parsedCustomFunctions.sort((s1, s2) -> (s2.length() - s1.length()));
        //hide funcs from replace
        for (int f = 0; f< parsedCustomFunctions.size(); f++) {
            String func = parsedCustomFunctions.get(f);
            withVars = withVars.replaceAll(func.split("[(]")[0], "{"+f+"}");
        }
        ArrayList<PrimitiveElement> primitiveElements = new ArrayList<>();

        for (String key : vars.keySet()) {
            //switch out variables in func unless override by local
            for (int f = 0; f< parsedCustomFunctions.size(); f++) {
                String func = parsedCustomFunctions.get(f);
                String expression = func.split("= ")[1].replaceAll(key, "("+vars.get(key)+")");
                if (contains(func.split(" =")[0].split("[(]")[1].replace("[)]", "").split(","), key)) {
                    parsedCustomFunctions.set(f, func.split("= ")[0] + "= " + expression);
                }
            }
            withVars = withVars.replaceAll(key, "("+vars.get(key)+")");
        }
        withVars = withVars.replaceAll("(\\d*),(\\d+)", "$1$2");


        for (int f = 0; f < parsedCustomFunctions.size(); f++) {
            withVars = withVars.replaceAll("[{]"+f+"[}]", parsedCustomFunctions.get(f).split("[(]")[0]);
            primitiveElements.add(new Function(parsedCustomFunctions.get(f)));
        }
        CalcMod.LOGGER.info("Parsed "+withVars);
            return new Expression(withVars, primitiveElements.toArray(new PrimitiveElement[0] )).calculate();
        }

    static boolean contains(String[] array, String value) {
        for (String str : array) {
            if (str.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static String getParsedStack(double items, int stacksize) {
        if (items >= 64) {
            return "Stacks: "+nf.format(Math.floor(items / stacksize))+", Items: "+ nf.format(items % stacksize);
        } else {
            return nf.format(items);
        }
    }


    public static void sendMessageServer(CommandSource source, String[] message, Boolean... isHelpMessage) throws CommandSyntaxException {
        var messageText = new TranslationTextComponent("");
        String m = "";
        for (var i = 0; i < message.length; i++) {
           if (i % 2 == 0) {
            messageText.append(new TranslationTextComponent(message[i]));
            m += message[i];
           } else {
            messageText.append(new TranslationTextComponent("§a"+message[i]+"§f").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message[i]))));
            m += message[i];
           }

        }


        if (isHelpMessage.length > 0) {
            if (isHelpMessage[0]) {
                source.getPlayerOrException().sendMessage(messageText, UUID.randomUUID());
                return;
            } 
        }
        messageText.append(new TranslationTextComponent(" "));
        source.getPlayerOrException().sendMessage(messageText.append(new TranslationTextComponent("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, m.replaceAll("§a", "").replaceAll("§f", ""))))), UUID.randomUUID());
    }

    


}