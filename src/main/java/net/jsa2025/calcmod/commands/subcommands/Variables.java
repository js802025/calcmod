package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Variables {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
        .then(CommandManager.literal("variables")
        .executes(ctx -> {
            CalcMessageBuilder message = execute();
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));
        return command;
    }

    public static CalcMessageBuilder execute() {
        String message = "§bVariables§f can be used inside equations in any number field. They act as shortcuts instead of having to remember that “a double chest full of 16 stackable items is 864.”  \nIf no stack size is given, variables default to the stack size of each command. \n\ndub: 3456 (default) \ndub64: 3456 \ndub16: 864 \ndub1: 54  \nsb: 1728 (default)  \nsb64: 1728  \nsb16: 432  \nsb1: 27  \nstack: 64 (default)  \nstack64: 64  \nstack16: 16  \nstack1: 1  \nmin: 60  \nhour: 3600  \nx: player x coord  \ny: player y coord  \nz: player z coord ";
        return new CalcMessageBuilder(message);
    }
}
