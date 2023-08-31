package net.jsa2025.calcmod.commands.subcommands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Overworld {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));



    public static String[] execute(ICommandSender sender, BlockPos... pos) {
        BlockPos position;
        position = pos[0];



        CalcMessageBuilder message = new CalcMessageBuilder().addInput("X: "+nf.format(position.getX())+" Z: "+nf.format(position.getZ())).addString(" §7>>§f Overworld = ").addResult("X: "+nf.format(position.getX()*8)+" Z: "+nf.format(position.getZ()*8));
        return message;
    }

    public static String helpMessage = "§b§LOverworld:§r§f \nGiven a block position in the Nether, returns the Overworld's corresponding coordinates. If no coordinates are given, command assumes current player position. \n§eUsage: /calc overworld <x> <y> <z>§f";


}
