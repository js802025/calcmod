package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;


public class Variables {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("variables")
        .executes(ctx -> {
            CalcMessageBuilder message = execute();
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        }));
        return command;
    }

    public static CalcMessageBuilder execute() {
        String message = """
            §bVariables§f can be used inside equations in any number field. They act as shortcuts instead of having to remember that “a double chest full of 16 stackable items is 864.”
            If no stack size is given, variables default to the stack size of each command.
            
                dub: 3456 (default)
                dub64: 3456
                dub16: 864
                dub1: 54
                sb: 1728 (default)
                sb64: 1728
                sb16: 432
                sb1: 27
                stack: 64 (default)
                stack64: 64
                stack16: 16
                stack1: 1
                min: 60
                hour: 3600
                x: player x coord
                y: player y coord
                z: player z coord
                health: player health
                """;
        return new CalcMessageBuilder(message);
    }
}
