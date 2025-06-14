package net.jsa2025.calcmod.commands;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.arguments.StringArgumentType;


import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.subcommands.*;

import net.jsa2025.calcmod.commands.subcommands.Random;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.PrimitiveElement;


import java.util.*;
import java.util.function.Supplier;

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static void register (CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registry) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("calc");
        command.then(Basic.buildClient()); // Corrected to buildClient
        command.then(Storage.buildClientNode());
        command.then(Nether.buildClientNode());
        command.then(Overworld.buildClientNode());
        command.then(SbToItem.buildClientNode());
        command.then(ItemToSb.buildClientNode());
        command.then(SecondsToHopperClock.buildClientNode());
        command.then(SecondsToRepeater.buildClientNode());
        command.then(ItemToStack.buildClientNode());
        command.then(StackToItem.buildClientNode());
        command.then(Rates.buildClientNode());
        command.then(AllayStorage.buildClientNode());
        command.then(Random.buildClientNode());
        command.then(Craft.buildClientNode()); // Craft.buildClientNode() no longer needs registry
        command.then(SignalToItems.buildClientNode());
        command.then(Piglin.buildClientNode()); // Piglin.java defines "barter"
        command.then(Custom.buildClientNode());
        command.then(Variables.buildClientNode());
        command.then(Help.buildClientNode());
        dispatcher.register(command);

    }
    
    public static void registerServer(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, RegistrationEnvironment env) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("calc");
        command.then(Basic.buildServer()); // Corrected to buildServer
        command.then(Storage.buildServerNode());
        command.then(Nether.buildServerNode());
        command.then(Overworld.buildServerNode());
        command.then(SbToItem.buildServerNode());
        command.then(ItemToSb.buildServerNode());
        command.then(SecondsToHopperClock.buildServerNode());
        command.then(SecondsToRepeater.buildServerNode());
        command.then(ItemToStack.buildServerNode());
        command.then(StackToItem.buildServerNode());
        command.then(Rates.buildServerNode());
        command.then(AllayStorage.buildServerNode());
        command.then(Random.buildServerNode());
        command.then(Craft.buildServerNode()); // Craft.buildServerNode() no longer needs registry
        command.then(SignalToItems.buildServerNode());
        command.then(Piglin.buildServerNode()); // Piglin.java defines "barter"
        command.then(Custom.buildServerNode());
        command.then(Variables.buildServerNode());
        command.then(Help.buildServerNode());

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
            vars.put("x", (double) player.getBlockPos().getX());
            vars.put("y", (double) player.getBlockPos().getX());
            vars.put("z", (double) player.getBlockPos().getZ());
            vars.put("health", (double) ((PlayerEntity) player).getHealth());
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
                if (!contains(func.split(" =")[0].split("[(]")[1].replace("[)]", "").split(","), key)) {
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
        source.getPlayer().sendMessage(messageText.append(Text.literal("§7[Click to Copy]§f").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, m.replaceAll("§a", "").replaceAll("§f", ""))))));
    }
    
    public static void sendMessage(FabricClientCommandSource source, CalcMessageBuilder messageBuilder) {
        source.sendFeedback(messageBuilder.generateStyledText());

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
        source.sendChatMessage(SentMessage.of(SignedMessage.ofUnsigned("hello")), true, MessageType.params(MessageType.SAY_COMMAND, source));
        source.sendMessage(messageText.append(Text.literal("§7[Click to Copy]§f").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, m.replaceAll("§a", "").replaceAll("§f", "")))))
                );
    }
    
    public static void sendMessageServer(ServerCommandSource source, CalcMessageBuilder messageBuilder) {
        source.sendFeedback(new Supplier<Text>() {
            @Override
            public Text get() {
                return messageBuilder.generateStyledText();
            }
        }, Objects.isNull(source.getPlayer()));

    }

    


}