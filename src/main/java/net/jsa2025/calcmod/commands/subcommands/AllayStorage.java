package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import org.bukkit.entity.Entity;

public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("allaystorage").then(Commands.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), StringArgumentType.getString(ctx, "itemsperhour"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })).then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }
    
//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
//        command
//        .then(CommandManager.literal("allaystorage").then(CommandManager.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"));
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })).then(CommandManager.literal("help").executes((ctx) -> {
//            CalcMessageBuilder message = Help.execute("allaystorage");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 1;
//        })));
//        return command;
//    }
//

    public static CalcMessageBuilder execute(Entity player, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour, 1);
        double ratesinsec = rates / 3600;
        double allaycooldown = 3;
        String allaystorage = nf.format(Math.ceil(ratesinsec/(1/allaycooldown)));

        return new CalcMessageBuilder().addString("Allays needed to sort ").addInput(itemsperhour).addString(" items/hr = ").addResult(allaystorage);
    }

    public static String helpMessage = """
        <aqua><bold>Allay Storage:<reset><white>
            Given the number of items per hour of a non stackable item <gray><italic>(can be in expression form)<reset><white>, returns allays needed to sort the item.
            <yellow>Usage: /calc allaystorage <numberofitems><white>
            """;


}
