package net.jsa2025.calcmod.commands.subcommands;


import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.ICommandSender;

public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    


    public static String[] execute(ICommandSender sender, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(sender.getPosition(), itemsperhour, 1);
        double ratesinsec = rates / 3600;
        double allaycooldown = 3;
        String allaystorage = nf.format(Math.ceil(ratesinsec/(1/allaycooldown)));

        String[] message = {"Allays Needed: ", allaystorage};
        return message;
    }

    public static String helpMessage = "§LAllay Storage:§r \nGiven the number of items per hour of a non stackable item, returns allays needed to sort the item. \n§cUsage: /calc allaystorage <numberofitems>§f";


}
