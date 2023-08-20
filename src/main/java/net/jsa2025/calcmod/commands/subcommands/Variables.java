package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
public class Variables {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("variables")
        .executes(ctx -> {
            String[] message = execute();
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }));
        return command;
    }

    public static String[] execute() {
        String message = "Dynamic variables will default to the stack size of each command. Here are the variables for the majority of commands which use a stack size of 64: \ndub: 3456(dynamic) \ndub64: 3456 \ndub16: 864 \ndub1: 54 \nsb: 1728(dynamic) \nsb64: 1728 \nsb16: 432 \nsb1: 27 \nstack: 64(dynamic) \nstack64: 64 \nstack16: 16 \nstack1: 1 \nmin: 60 \nhour: 3600";
                String[] m = {message};
        return m;
    }
}
