package net.jsa2025.calcmod.commands.subcommands;




import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.ContainerSuggestionProvider;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;







public class SignalToItems {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("signaltoitems")
        .then(Commands.argument("container", StringArgumentType.string()).suggests(new ContainerSuggestionProvider())
        .then(Commands.argument("signal", StringArgumentType.greedyString()).executes((ctx) -> {
            String[] message = execute(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "container"), StringArgumentType.getString(ctx, "signal"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }))).then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("signaltoitems");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })
        ));
        return command;
    }

    public static String[] execute(ServerPlayer player, String container, String signal) {
        double strength = CalcCommand.getParsedExpression(player.getOnPos(), signal);
        var containers = ContainerSuggestionProvider.containers;
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

    public static String helpMessage = """
        §LSignal To Items:§r
           Given a container and a desired signal strength from a comparator, returns the number of items needed to achieve that signal strength.
            §cUsage: /calc signaltoitems <container> <signal>§f
                """;

}

