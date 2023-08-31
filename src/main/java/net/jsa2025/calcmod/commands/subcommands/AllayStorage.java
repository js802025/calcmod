package net.jsa2025.calcmod.commands.subcommands;


import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.ICommandSender;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;

public class AllayStorage {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    


    public static CalcMessageBuilder execute(ICommandSender sender, String itemsperhour) {
        double rates = CalcCommand.getParsedExpression(sender.getCommandSenderEntity(), itemsperhour, 1);
        double ratesinsec = rates / 3600;
        double allaycooldown = 3;
        String allaystorage = nf.format(Math.ceil(ratesinsec/(1/allaycooldown)));

        return new CalcMessageBuilder().addString("Allays needed to sort ").addInput(itemsperhour).addString(" items/hr = ").addResult(allaystorage);
    }
    public static String helpMessage = "§LAllay Storage:§r \nGiven the number of items per hour of a non stackable item, returns allays needed to sort the item \n§cUsage: /calc allaystorage <numberofitems>§f";


}
