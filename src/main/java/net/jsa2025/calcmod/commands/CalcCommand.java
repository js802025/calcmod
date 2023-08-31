package net.jsa2025.calcmod.commands;

import java.text.DecimalFormat;
import java.text.NumberFormat;


import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.subcommands.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;


import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

import net.minecraft.entity.Entity;
import org.apache.logging.log4j.Level;
import org.mariuszgromada.math.mxparser.Expression;
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

public class CalcCommand  extends CommandBase {
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

//    public static void registerServer(CommandDispatcher<CommandSource> dispatcher, boolean dedicated) {
//        LiteralArgumentBuilder<CommandSource> command = Commands.literal("calc");
//        command = Basic.registerServer(command);
//        command = Storage.registerServer(command);
//        command = Nether.registerServer(command);
//        command = Overworld.registerServer(command);
//        command = SbToItem.registerServer(command);
//        command = ItemToSb.registerServer(command);
//        command = SecondsToHopperClock.registerServer(command);
//        command = SecondsToRepeater.registerServer(command);
//        command = ItemToStack.registerServer(command);
//        command = StackToItem.registerServer(command);
//        command = Rates.registerServer(command);
//        command = AllayStorage.registerServer(command);
//        command = Random.registerServer(command);
//        command = Craft.registerServer(command);
//        command = SignalToItems.registerServer(command);
//        command = Variables.registerServer(command);
//        command = Help.registerServer(command);
//
//        dispatcher.register(command);
//
//        LiteralArgumentBuilder<CommandSource> copyCommand = Commands.literal("copy")
//                .then(Commands.argument("text", StringArgumentType.string())
//                        .executes(ctx -> {
//                            Minecraft.getInstance().keyboardListener.setClipboardString(StringArgumentType.getString(ctx, "text"));
//                            return 0;
//                        }));
//
//        dispatcher.register(copyCommand);
//    }

    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/calc";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        String expression = "";
//        for (String i :args) {
//            expression += i+" ";
//        }
//        double answer = getParsedExpression(sender.getCommandSenderEntity(), expression);
//        String[] message = {expression+" = ", nf.format(answer)};
//        CalcCommand.sendMessageServer(sender, message);
        CalcMod.LOGGER.log(Level.INFO, "Expression: "+parseExpressionText(args, 0));

        CalcMod.LOGGER.log(Level.INFO, args.length);
        CalcMessageBuilder message = new CalcMessageBuilder("");
        if (args.length > 0) {

            if (args[0].contains("nether")) {

                if (args.length > 1) {
                    if (args[1].contains("help")) message = Help.execute("nether");
                    else message = Nether.execute(parseBlockPos(sender, args, 1, true));
                } else {
                    message = Nether.execute(sender.getPosition());
                }
            } else if (args[0].contains("overworld")) {
                if (args.length > 1) {
                    if (args[1].contains("help")) message = Help.execute("overworld");
                    else message = Nether.execute(parseBlockPos(sender, args, 1, true));
                } else {
                    message = Nether.execute(sender.getPosition());
                }
            } else if (args[0].contains("allaystorage")) {
                if (args[1].contains("help")) message = Help.execute("allaystorage");
                else message = AllayStorage.execute(sender, args[1]);

            } else if (args[0].contains("craft")) {
                CalcMod.LOGGER.log(Level.INFO, "Expression: " + args[1] + " ID:" + CraftingManager.getRecipeById(300).getRegistryName().toString());
                if (args[1].contains("help")) message = Help.execute("craft");

                else
                    message = Craft.execute(sender, CraftingManager.getRecipe(new ResourceLocation(args[1])), parseExpressionText(args, 2));
            } else if (args[0].contains("itemtosb")) {
                if (args[1].contains("help")) message = Help.execute("itemtosb");
                else if (args[1].contains("16s")) message = ItemToSb.execute(sender, parseExpressionText(args, 2), 16);
                else if (args[1].contains("1s")) message = ItemToSb.execute(sender, parseExpressionText(args, 2), 1);
                else message = ItemToSb.execute(sender, parseExpressionText(args, 2), 64);
            } else if (args[0].contains("itemtostack")) {
                if (args[1].contains("help")) message = Help.execute("itemtostack");
                else if (args[1].contains("16s"))
                    message = ItemToStack.execute(sender, parseExpressionText(args, 2), 16);
                else if (args[1].contains("1s")) message = ItemToStack.execute(sender, parseExpressionText(args, 2), 1);
                else message = ItemToStack.execute(sender, parseExpressionText(args, 2), 64);
            } else if (args[0].contains("barter")) {
                if (args[1].contains("help")) message = Help.execute("barter");
                else if (args[1].contains("toitem")) message = Piglin.executeToItems(sender, args[2], args[3]);
                else message = Piglin.executeToGold(sender, args[2], args[3]);
            } else if (args[0].contains("random")) {
                if (args[1].contains("help")) message = Help.execute("random");
                else if (args[1].contains("minmax")) message = Random.execute(sender, args[2], args[3]);
                else if (args[1].contains("max")) message = Random.execute(sender, args[2]);
            } else if (args[0].contains("rates")) {
                if (args[1].contains("help")) message = Help.execute("rates");
                else message = Rates.execute(sender, args[1], parseExpressionText(args, 2));
            } else if (args[0].contains("sbtoitem")) {
                if (args[1].contains("help")) message = Help.execute("sbtoitem");
                else if (args[1].contains("16s")) message = SbToItem.execute(sender, parseExpressionText(args, 2), 16);
                else if (args[1].contains("1s")) message = SbToItem.execute(sender, parseExpressionText(args, 2), 1);
                else message = SbToItem.execute(sender, parseExpressionText(args, 2), 64);
            } else if (args[0].contains("secondstohopperclock")) {
                if (args[1].contains("help")) message = Help.execute("secondstohopperclock");
                else message = SecondsToHopperClock.execute(sender, parseExpressionText(args, 1));
            } else if (args[0].contains("secondstorepeater")) {
                if (args[1].contains("help")) message = Help.execute("secondstorepeater");
                else message = SecondsToRepeater.execute(sender, parseExpressionText(args, 1));
            } else if (args[0].contains("signaltoitems")) {
                if (args[1].contains("help")) message = Help.execute("signaltoitems");
                else message = SignalToItems.execute(sender, args[1], parseExpressionText(args, 2));
            } else if (args[0].contains("stacktoitems")) {
                if (args[1].contains("help")) message = Help.execute("stacktoitems");
                else if (args[1].contains("16s"))
                    message = StackToItem.execute(sender, parseExpressionText(args, 2), 16);
                else if (args[1].contains("1s")) message = StackToItem.execute(sender, parseExpressionText(args, 2), 1);
                else message = StackToItem.execute(sender, parseExpressionText(args, 2), 64);
            } else if (args[0].contains("storage")) {
                if (args[1].contains("help")) message = Help.execute("storage");
                else if (args.length > 2) message = Storage.execute(sender, args[1], parseInt(args[2]));
                else message = Storage.execute(sender, parseExpressionText(args, 1), 1);
            } else if (args[0].contains("custom")) {
                if (args[1].contains("help")) message = Help.execute("custom");
                else if (args[1].contains("add")) message = Custom.executeAdd(sender, args[2], parseExpressionText(args, 3));
                else if (args[1].contains("list")) message = Custom.executeList(sender);
                else if (args[1].contains("remove")) message = Custom.executeRemove(sender, args[2]);
                else if (args[1].contains("run")) message = Custom.executeRun(sender, parseExpressionText(args, 2));
            } else if (args[0].contains("variables")) {
                message = Variables.execute();

        }else {
                message = Basic.execute(sender, parseExpressionText(args, 0));
            }
            sendMessageServer(sender, message);
        }



    }

    public static String parseExpressionText(String[] args, int startIndex) {
        String expression = "";
        for (int i = 0; i < args.length; i++) {
            if (i >= startIndex) {
                expression += args[i] + " ";
            }
        }
        return expression;
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
            vars.put("x", (double) player.getPosition().getX());
            vars.put("y", (double) player.getPosition().getX());
            vars.put("z", (double) player.getPosition().getZ());
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


    public static void sendMessageServer(ICommandSender source, String[] message, Boolean... isHelpMessage) {
        TextComponentString messageText = new TextComponentString("");
        String m = "";
        for (int i = 0; i < message.length; i++) {
           if (i % 2 == 0) {
            messageText.appendSibling(new TextComponentString(message[i]));
            m += message[i];
           } else {
            messageText.appendSibling(new TextComponentString("§a"+message[i]+"§f").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copy \""+message[i]+'"'))));
            m += message[i];
           }

        }


        if (isHelpMessage.length > 0) {
            if (isHelpMessage[0]) {
                source.sendMessage(messageText);
                return;
            } 
        }
        messageText.appendSibling(new TextComponentString(" "));
        source.sendMessage(messageText.appendSibling(new TextComponentString("\2473[Click To Copy]").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copy \""+m.replaceAll("§a", "").replaceAll("§f", "")+'"')))));
    }

    public static void sendMessageServer(ICommandSender source, CalcMessageBuilder messageBuilder) {
        source.sendMessage(messageBuilder.generateStyledText());
    }

    


}