package net.jsa2025.calcmod.commands.subcommands;





import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.command.ICommandSender;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;







public class SignalToItems {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("signaltoitems")
//        .then(Commands.argument("container", StringArgumentType.string()).suggests(new ContainerSuggestionProvider())
//        .then(Commands.argument("signal", StringArgumentType.greedyString()).executes((ctx) -> {
//            String[] message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "container"), StringArgumentType.getString(ctx, "signal"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }))).then(Commands.literal("help").executes(ctx -> {
//            String[] message = Help.execute("signaltoitems");
//            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
//            return 0;
//        })
//        ));
//        return command;
//    }
public static Map<String, Integer> containers;

    static {
        containers = new HashMap<>();
        containers.put("shulker_box", 27);
        containers.put("chest", 27);
        containers.put("barrel", 27);
        containers.put("trapped_chest", 27);
        containers.put("double_chest", 54);
        containers.put("dropper", 9);
        containers.put("dispenser", 9);
        containers.put("hopper",5);
        containers.put("hopper_minecart", 5);
        containers.put("brewing_stand", 5);
        containers.put("furnace", 3);
        containers.put("blast_furnace", 3);
        containers.put("smoker", 3);
    }

    public static String[] execute(ICommandSender sender, String container, String signal) {
        double strength = CalcCommand.getParsedExpression(sender.getPosition(), signal);
        double stackAmount = containers.get(container);
        double secondlevel = (stackAmount*32)/7;
        double item64 = Math.max(strength, Math.ceil(secondlevel*(strength-1)));
        String stackable16 = "Not Possible";
        String stackable1 = "Not Possible";
        double secondlevel16 = (stackAmount*8)/7;
        double item16 = Math.max(strength, Math.ceil(secondlevel16*(strength-1)));
        double item16nextstrength = Math.ceil(secondlevel16*(strength));
        double secondlevel1 = (stackAmount)/14;
        double item1 = Math.ceil(secondlevel1*(strength-1));
        if (item1 < 0)  {
            item1 = 0;
        } else if (item1 == 0) {
            item1 = 1;
        }
        double item1nextstrength = Math.ceil(secondlevel1*(strength));
        if (item16nextstrength > item16) {
            stackable16 = CalcCommand.getParsedStack(item16, 16);
        }
        if (item1nextstrength > item1) {
            stackable1 = nf.format(item1);
        }

        return new String[] {"Items Required for 64 Stackable: ", CalcCommand.getParsedStack(item64, 64), "\nItems Required for 16 Stackable: ", stackable16, "\nItems Required for Non Stackable: ", stackable1};
    }

    public static String helpMessage = "§LSignal To Items:§r \nGiven a container and a desired signal strength from a comparator, returns the number of items needed to achieve that signal strength. \n§cUsage: /calc signaltoitems <container> <signal>§f";

}

