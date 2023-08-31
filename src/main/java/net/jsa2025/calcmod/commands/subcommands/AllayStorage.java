package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;

import net.minecraft.command.Commands;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;

public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("allaystorage").then(Commands.argument("itemsperhour", StringArgumentType.greedyString()).executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), StringArgumentType.getString(ctx, "itemsperhour"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })).then(Commands.literal("help").executes((ctx) -> {
            CalcMessageBuilder message = Help.execute("allaystorage");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }


    public static CalcMessageBuilder execute(Entity player, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(player, itemsperhour, 1);
        double ratesinsec = rates / 3600;
        double allaycooldown = 3;
        String allaystorage = nf.format(Math.ceil(ratesinsec/(1/allaycooldown)));

        return new CalcMessageBuilder().addString("Allays needed to sort ").addInput(itemsperhour).addString(" items/hr = ").addResult(allaystorage);
    }
    public static String helpMessage = "§LAllay Storage:§r \nGiven the number of items per hour of a non stackable item, returns allays needed to sort the item \n§cUsage: /calc allaystorage <numberofitems>§f";


}
