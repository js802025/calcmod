package net.jsa2025.calcmod.commands.subcommands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));



    public static String[] execute(ICommandSender sender, BlockPos... pos) {
        BlockPos position;
        position = pos[0];


        String[] message = {"Overworld Coords: ", "X: "+nf.format(position.getX()*8)+" Z: "+nf.format(position.getZ()*8)};
        return message;
    }

    public static String helpMessage = "§LOverworld:§r \nGiven a block position in the nether, returns the overworld coordinates \n§cUsage: /calc overworld <x> <y> <z>§f";


}
