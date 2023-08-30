package net.jsa2025.calcmod.commands.subcommands;

import com.google.gson.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.CustomFunctionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Custom {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    public static final File commandFile = new File(".", "config/calcmod.json");

    
    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command = command.then(Commands.literal("custom")
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("function", StringArgumentType.greedyString())
                                        .executes((ctx) -> {
                                            String function = StringArgumentType.getString(ctx, "function");
                                            String name = StringArgumentType.getString(ctx, "name");
                                            CalcMessageBuilder messageBuilder;
                                            if (!Pattern.matches(".*\\d.*", name) && !parseEquationVariables(function).isEmpty()) {
                                                saveNewCommand(name, function);
                                                messageBuilder = new CalcMessageBuilder("§eAdded "+name+"§f");
                                            } else if (parseEquationVariables(function).isEmpty()) {
                                                messageBuilder = new CalcMessageBuilder("§cMust have at least one parameter.§f");
                                            }else {
                                                messageBuilder = new CalcMessageBuilder("§cCannot have numbers in command name.§f");
                                            }


                                            CalcCommand.sendMessageServer(ctx.getSource(), messageBuilder);

                                            return 0;
                                        }))))
                .then(Commands.literal("list").executes(ctx -> {
                    JsonObject fs = getFunctions();
                    String m = fs.entrySet().stream().map(entry -> "§b§L"+entry.getKey() + ":§f§r " + entry.getValue().getAsString()).collect(Collectors.joining("\n"));
                    CalcMessageBuilder messageBuilder = new CalcMessageBuilder(m);
                    CalcCommand.sendMessageServer(ctx.getSource(), messageBuilder);
                    return 0;
                }))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(new CustomFunctionProvider())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    deleteCommand(name);
                                    CalcMessageBuilder messageBuilder = new CalcMessageBuilder("§cRemoved "+name+"§f");
                                    CalcCommand.sendMessageServer(ctx.getSource(), messageBuilder);
                                    return 0;
                                })))
                .then(Commands.literal("run")
                        .then(Commands.argument("function", StringArgumentType.greedyString())
                                .suggests(new CustomFunctionProvider())
                                .executes((ctx) -> {
                                    String eqn = StringArgumentType.getString(ctx, "function");
                                    double result = CalcCommand.getParsedExpression(ctx.getSource().getEntity(), eqn);
                                    CalcMessageBuilder messageBuilder = new CalcMessageBuilder(CalcMessageBuilder.MessageType.BASIC, new String[] {eqn}, new String[] {nf.format(result)});
                                    CalcCommand.sendMessageServer(ctx.getSource(), messageBuilder);
                                    return 0;
                                }))



        ).then(Commands.literal("help").executes(ctx -> {
                            CalcCommand.sendMessageServer(ctx.getSource(), Help.execute("custom"));
                            return 0;
                        }))

        );


        return command;
    }


    public static ArrayList<String> parseEquationVariables(String input) {
        String patternString = "\\[([^\\]]+)\\]";//"\\[[^\\]]+\\]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        StringBuilder commandJson = new StringBuilder("{\n\"variables\": [\n");
        ArrayList<String> variables = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            group = group.substring(1, group.length()-1);
          //  commandJson.append("\"").append(group).append("\",\n");
            if (!variables.contains(group)) {
                variables.add(group);
            }
        }
        commandJson.append("],\n\"equation\": \"").append(input).append("\"\n}");
      //  CalcMod.LOGGER.info(commandJson.toString());
        return variables;
    }

    public static JsonObject readJson() {
         try (BufferedReader reader = new BufferedReader(new FileReader(commandFile))) {
            JsonObject tempJson;
            try {
                tempJson = new JsonParser().parse(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject();
            } catch (Exception ignored) {
                tempJson = new JsonObject();
            }
            JsonObject json = tempJson; // annoying lambda requirement
            return json;
        } catch (Exception ignored) { return new JsonObject();}

    }


    public static JsonObject getFunctions() {
        JsonObject json = readJson();
        if (!json.has("functions")) {
            json = new JsonObject();
            json.add("functions", new JsonObject());
        }
        return json.getAsJsonObject("functions");
    }

    public static ArrayList<String> getParsedFunctions() {
        ArrayList<String> parsedFuncs = new ArrayList<>();
        JsonObject funcs = getFunctions();
        for (Map.Entry<String, JsonElement> f : funcs.entrySet()) {
            String func = f.getValue().getAsString();
            ArrayList<String> vars = parseEquationVariables(func);
            if (!vars.isEmpty()) {
                String combinedVars = String.join(", ", vars);
                parsedFuncs.add(f.getKey() + "(" + combinedVars + ") = " + func.replace("[", "").replace("]", ""));
            } else {
                parsedFuncs.add(f.getKey()+"() = " + func);
            }
            CalcMod.LOGGER.info("Parsed Func "+parsedFuncs);
        }
        return parsedFuncs;
    }

    public static void saveNewCommand(String name, String eqn) {
        JsonObject json = readJson();
        if (!json.has("functions")) {
            json = new JsonObject();
            json.add("functions", new JsonObject());
        }
        json.getAsJsonObject("functions").addProperty(name, eqn);
        saveCommandsFile(json);

    }

    public static void deleteCommand(String name) {
        JsonObject json = readJson();
        json.getAsJsonObject("functions").remove(name.split("[(]")[0]);
        saveCommandsFile(json);
    }
    public static void saveCommandsFile(JsonObject json) {
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = new File(".", "config");
        if ((dir.exists() && dir.isDirectory() || dir.mkdirs()) && !commandFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                commandFile.createNewFile();
                
            } catch (IOException ignored) {}
        }
         try {
        FileWriter writer = new FileWriter(commandFile);
                writer.write(gson.toJson(json));
                writer.close();
                  } catch (IOException ignored) {}
    }

    public static String helpMessage = """
            §b§LCustom:§r§f
                Custom functions are reusable commands that perform a specific computation. They can be run in any number field formatted with the function name and the parameters in parenthesis §7§o(customfunction(param1, paramN))§r§f.
                Custom functions can have any number of parameters, specified in [square] brackets when adding the function.
                §eUsage: /calc custom add <name> <function>§f
                §eUsage: /calc custom run <name> <input>§f
                §eUsage: /calc <name>(<parameters>)§f
                §eUsage: /calc custom list§f
                §eUsage: /calc custom remove <name>§f
                    """;

}
