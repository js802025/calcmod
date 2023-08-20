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
                String[] m = {message};
        return m;
    }
}
