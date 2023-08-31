package net.jsa2025.calcmod.commands.subcommands;



import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Variables {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


//    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
//        command
//        .then(Commands.literal("variables")
//        .executes(ctx -> {
//            String[] message = execute();
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        }));
//        return command;
//    }

    public static CalcMessageBuilder execute() {
        String message = "§bVariables§f can be used inside equations in any number field. They act as shortcuts instead of having to remember that “a double chest full of 16 stackable items is 864.” \nIf no stack size is given, variables default to the stack size of each command.  \ndub: 3456 (default)  \ndub64: 3456  \ndub16: 864  \ndub1: 54  \nsb: 1728 (default)  \nsb64: 1728  \nsb16: 432  \nsb1: 27  \nstack: 64 (default)  \nstack64: 64  \nstack16: 16  \nstack1: 1  \nmin: 60  \nhour: 3600  \nx: player x coord  \ny: player y coord  \nz: player z coord ";
        return new CalcMessageBuilder(message);
    }
}
