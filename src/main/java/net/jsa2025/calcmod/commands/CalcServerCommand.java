package net.jsa2025.calcmod.commands;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;


//import static dev.xpple.clientarguments.arguments.CBlockPosArgumentType.*;
import static net.minecraft.command.argument.BlockPosArgumentType.*;
import java.util.Locale;

public class CalcServerCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US")); 
    public static void register (CommandDispatcher<ServerCommandSource> dispacther, CommandRegistryAccess registry, RegistrationEnvironment env) {
        dispacther.register(CommandManager.literal("calc")
        .then(CommandManager.argument("expression", StringArgumentType.greedyString()).executes(ctx -> executeBasicCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "expression"))))
        .then(CommandManager.literal("storage").then(CommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1))
        .then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")))))
        .then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "itemsperhour"), 1)))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "storage"))))
        .then(CommandManager.literal("nether")
        .executes(ctx -> executeNetherCoord(ctx.getSource()))
        .then(CommandManager.argument("pos", blockPos())
        .executes(ctx -> executeNetherCoord(ctx.getSource(), getBlockPos(ctx, "pos"))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "nether"))))
        .then(CommandManager.literal("overworld").then(CommandManager.argument("pos", blockPos())
        .executes(ctx -> executeOverworldCoord(ctx.getSource(), getBlockPos(ctx, "pos"))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "overworld"))))
        .then(CommandManager.literal("sbtoitem").then(CommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes(ctx -> executeSbToItem(ctx.getSource(), StringArgumentType.getString(ctx, "numberofsbs"))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "sbtoitem"))))
        .then(CommandManager.literal("itemtosb").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> executeItemToSb(ctx.getSource(), StringArgumentType.getString(ctx, "numberofitems"))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "itemtosb"))))
        .then(CommandManager.literal("secondstohopperclock").then(CommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> executeSecondsToHopperClock(ctx.getSource(), StringArgumentType.getString(ctx, "seconds"))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "seconds2hopperclock"))))
        .then(CommandManager.literal("secondstorepeater").then(CommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> executeSecondsToRepeater(ctx.getSource(), StringArgumentType.getString(ctx, "seconds"))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "seconds2repeater"))))
        .then(CommandManager.literal("itemtostack").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> executeItemToStack(ctx.getSource(), StringArgumentType.getString(ctx, "numberofitems"), 64)))
        .then(CommandManager.literal("16s").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> executeItemToStack(ctx.getSource(), StringArgumentType.getString(ctx, "numberofitems"), 16))))
        .then(CommandManager.literal("1s").then(CommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> executeItemToStack(ctx.getSource(), StringArgumentType.getString(ctx, "numberofitems"), 1))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "itemtostack"))))
        .then(CommandManager.literal("stacktoitem").then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> executeStackToItem(ctx.getSource(), StringArgumentType.getString(ctx, "numberofstacks"), 64)))
        .then(CommandManager.literal("16s").then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> executeStackToItem(ctx.getSource(), StringArgumentType.getString(ctx, "numberofstacks"), 16))))
        .then(CommandManager.literal("1s").then(CommandManager.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> executeStackToItem(ctx.getSource(), StringArgumentType.getString(ctx, "numberofstacks"), 1))))
        .then(CommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "stacktoitem"))))
        .then(CommandManager.literal("variables")
        .executes(ctx -> executeVariables(ctx.getSource())))

        .then(CommandManager.literal("help")
        .executes(ctx -> executeHelp(ctx.getSource(), "")))

        );


    }

    public static int executeBasicCalculation(ServerCommandSource source, String expression) {
        Expression e = new Expression(getParsedString(expression));
        String[] message = {"Result: ", nf.format(e.calculate())};
        sendMessage(source, message);
        return 1;
        
    }

    public static int executeStorageCalculation(ServerCommandSource source, String itemsperhour, int timesHopperSpeed) {
        double rates = new Expression(getParsedString(itemsperhour)).calculate();
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        String[] message = {"Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): ", nf.format(sorters), " \nSbs per hour: ", nf.format(sbsperhour)};
        
        sendMessage(source, message);
        return 1;
    }

    public static int executeNetherCoord(ServerCommandSource source, BlockPos... pos ) {
        BlockPos position;
        if (pos.length == 0) {
            position = source.getPlayer().getBlockPos();
        } else {
            position = pos[0];
        }
        source.getPlayer().sendMessage(Text.literal("Nether Coord: §aX: " + nf.format(position.getX()/8) + " Z: " + nf.format(position.getZ()/8) + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + nf.format(position.getX()/8) + " Z: " + nf.format(position.getZ()/8))))));
        return 1;
    }

    public static int executeOverworldCoord(ServerCommandSource source, BlockPos... pos ) {
        BlockPos position;
        if (pos.length == 0) {
            position = source.getPlayer().getBlockPos();
        } else {
            position = pos[0];
        }
        source.getPlayer().sendMessage(Text.literal("Overworld Coord: §aX: " + nf.format(position.getX()*8) + " Z: " + nf.format(position.getZ()*8) + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + nf.format(position.getX()*8) + " Z: " + nf.format(position.getZ()*8))))));
        return 1;
    }

    public static int executeSbToItem(ServerCommandSource source, String numberofsbs) {
        double sbs = new Expression(getParsedString(numberofsbs, 1)).calculate();
        double items = sbs * 1728;
        String message[] = {"Items: ", nf.format(items)};
        sendMessage(source, message);
        return 1;
    }

    public static int executeItemToSb(ServerCommandSource source, String numberofitems) {
        double items = new Expression(getParsedString(numberofitems)).calculate();
        double sbs = items / 1728;
        String[] message= {"Sbs: ", nf.format(sbs)};


        sendMessage(source, message);
        return 1;
    }

    public static int executeSecondsToHopperClock(ServerCommandSource source, String seconds) {
        double secondsDouble = new Expression(getParsedString(seconds)).calculate();
        double hopperclock = Math.ceil(secondsDouble *1.25);
        String[] message = {"Hopper Clock Items: ", nf.format(hopperclock)};
        sendMessage(source, message);
        return 1;
    }

    public static int executeSecondsToRepeater(ServerCommandSource source, String seconds) {
        double secondsDouble = new Expression(getParsedString(seconds)).calculate();
        double ticks = secondsDouble * 10;
        double repeaters = Math.ceil(ticks/4);
        if (ticks % 4 != 0) {
            String[] message = {"Repeaters Required: ", nf.format(repeaters), " \nLast Repeater Tick: ", nf.format(ticks % 4)} ;
            sendMessage(source, message);
        } else {
            String[] message = {"Repeaters Required: ", nf.format(Math.ceil(ticks/4))};
            sendMessage(source, message);
        }
        return 1;
    }
    
    public static int executeItemToStack(ServerCommandSource source, String numberofitems, int stackSize) {
        double items = new Expression(getParsedString(numberofitems, stackSize)).calculate();
        double stacks = Math.floor(items/stackSize);
        double leftover = items % stackSize;
        String[] message = {"Stacks: ",  nf.format(stacks), " \nLeftover Items: ",  nf.format(leftover)};
        sendMessage(source, message);        
        return 1;
        
    }

    public static int executeStackToItem(ServerCommandSource source, String numberofstacks, int stackSize) {
        double stacks = new Expression(getParsedString(numberofstacks, 1)).calculate();
        double items = stacks * stackSize;
        String[] message = {"Items: ", nf.format(items)};
        sendMessage(source, message);
        return 1;
    }

    public static int executeVariables(ServerCommandSource source) {
        String message = """
            Dynamic variables will default to the stack size of each command. Here are the variables for the majority of commands which use a stack size of 64:
                dub: 3456(dynamic)
                dub64: 3456
                dub16: 864
                dub1: 54
                sb: 1728(dynamic)
                sb64: 1728
                sb16: 432
                sb1: 27
                stack: 64(dynamic)
                stack64: 64
                stack16: 16
                stack1: 1
                min: 60
                hour: 3600
                """;
                source.getPlayer().sendMessage(Text.literal(message));
        return 1;
    }

    public static int executeHelp(ServerCommandSource source, String help) {
        var helpMessage = "";
        if (help == "storage") {
            helpMessage = """
            §LStorage:§r
                Given rate in terms of items per hour(can be in expression form) and optionally hopper speed returns the number of needed sorters and rates in terms of sbs per hour
                §cUsage: /calc storage <itemsperhour>
                Usage: /calc storage <timesHopperSpeed> <itemsperhour> §f
                    """;
        } else if (help == "nether") {
            helpMessage = """
            §LNether:§r
                Given a block position returns the nether coordinates
                §cUsage: /calc nether <x> <y> <z>§f
                    """;
        } else if (help == "overworld") {
            helpMessage = """
            §LOverworld:§r
                Given a block position returns the overworld coordinates
                §cUsage: /calc overworld <x> <y> <z>§f
                    """;
        } else if (help == "sbtoitem") {
            helpMessage = """
            §LSb to Item:§r
                Given a number of sbs (can be in expression form) returns the number of items
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                §cUsage: /calc sbtoitem <numberofsbs>§f
                    """;
        } else if (help == "itemtosb") {
            helpMessage = """
            §LItem to Sb:§r
                Given a number of items (can be in expression form) returns the number of sbs
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                §cUsage: /calc itemtosb <numberofitems>§f
                    """;
        } else if (help == "seconds2hopperclock") {
            helpMessage = """
            §LSeconds to Hopper Clock:§r
                Given a number of seconds (can be in expression form) returns the number of ticks in a hopper clock
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                §cUsage: /calc seconds2hopperclock <seconds>§f
                    """;
        } else if (help == "seconds2repeater") {
            helpMessage = """
            §LSeconds to Repeater:§r
                Given a number of seconds (can be in expression form) returns the number of repeaters and the last tick of the last repeater
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                §cUsage: /calc seconds2repeater <seconds>§f
                    """;
        } else if (help == "itemtostack") {
            helpMessage = """
            §LItem to Stack:§r
                Given a number of items (can be in expression form) returns the number of stacks and leftover items
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                §cUsage: /calc itemtostack <numberofitems>§f
                    """;
        } else if (help == "stacktoitem") {
            helpMessage = """
            §LStack to Item:§r
                Given a number of stacks (can be in expression form) returns the number of items
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                §cUsage: /calc stacktoitem <numberofstacks>§f
                    """;
                } else {
            helpMessage = """
            Calcmod 

            §LBasic(no arguments):§r
                Functions like a simple calculator but with variables, to see the variables available run /calc variables
                §cUsage: /calc <expression>§f
            
            §LStorage:§r
                Given rate in terms of items per hour (can be in expression form) and optionally hopper speed returns the number of needed sorters and rates in terms of sbs per hour
                §cUsage: /calc storage <itemsperhour>
                Usage: /calc storage <timesHopperSpeed> <itemsperhour>§f
            
            §LNether:§r
                Given a block position returns the nether coordinates
                §cUsage: /calc nether <x> <y> <z>§f
            
            §LOverworld:§r
                Given a block position returns the overworld coordinates
                §cUsage: /calc overworld <x> <y> <z>§f
            
            §LSb to Item:§r
                Given a number of sbs (can be in expression form) returns the number of items
                §cUsage: /calc sbtoitem <numberofsbs>§f
            
            §LItem to Sb:§r
                Given a number of items (can be in expression form) returns the number of sbs
                §cUsage: /calc itemtosb <numberofitems>§f
            
            §LSeconds to Hopper Clock:§r
                Given a number of seconds (can be in expression form) returns the number of ticks in a hopper clock
                §cUsage: /calc secondstohopperclock <seconds>§f
            
            §LSeconds to Repeater:§r
                Given a number of seconds (can be in expression form) returns the number of repeaters and the last tick of the last repeater
                §cUsage: /calc secondstorepeater <seconds>§f

            §LItem to Stack:§r
                Given a number of items (can be in expression form) returns the number of stacks and leftover items
                §cUsage: /calc itemtostack <numberofitems>§f

            §LStack to Item:§r
                Given a number of stacks (can be in expression form) returns the number of items
                §cUsage: /calc stacktoitem <numberofstacks>§f
                """;;
        }

        source.getPlayer().sendMessage(Text.of(helpMessage));
        return 1;
    }

    private static String getParsedString(String in, Integer... nonstackable) {
        if (nonstackable.length > 0) {
        if (nonstackable[0] == 1) {
            return in.replaceAll("dub", "*54").replaceAll("sb", "*27").replaceAll("stack", "*1").replaceAll("min", "*60").replaceAll("hour", "*3600");
        } else if (nonstackable[0] == 16) {
            return in.replaceAll("dub", "*864").replaceAll("sb", "*432").replaceAll("stack", "*16").replaceAll("min", "*60").replaceAll("hour", "*3600");
        }
    }
        return in.replaceAll("dub", "*3456").replaceAll("sb", "*1728").replaceAll("stack", "*64").replaceAll("min", "*60").replaceAll("hour", "*3600");
    }

    private static void sendMessage(ServerCommandSource source, String... message) {
        var messageText = Text.literal("");
        for (var i = 0; i < message.length; i++) {
           if (i % 2 == 0) {
            messageText.append(Text.literal(message[i]));
           } else {
            messageText.append(Text.literal("§a"+message[i]+"§f").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message[i]))));
           }
           
        }
        source.getPlayer().sendMessage(messageText.append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, messageText.toString().replaceAll("§a", "").replaceAll("§f", ""))))));
    }


}