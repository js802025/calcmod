package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.arguments.CIdentifierArgumentType;
import net.jsa2025.calcmod.commands.arguments.CRecipeSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command, CommandRegistryAccess registry) {
        command
        .then(ClientCommandManager.literal("craft").then(ClientCommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new CRecipeSuggestionProvider())
                        .then(ClientCommandManager.literal("depth").then( ClientCommandManager.argument("level", IntegerArgumentType.integer())
        .then(ClientCommandManager.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), CIdentifierArgumentType.getRecipeArgument(ctx, "item").value(), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().getRegistryManager());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        ).then(ClientCommandManager.argument("amount", StringArgumentType.greedyString())
                        .executes((ctx) -> {
                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), CIdentifierArgumentType.getRecipeArgument(ctx, "item").value(), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().getRegistryManager());
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        })))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    
    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command, CommandRegistryAccess registry) {
        command
        .then(CommandManager.literal("craft").then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new RecipeSuggestionProvider())
        .then(CommandManager.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), IdentifierArgumentType.getRecipeArgument(ctx, "item").value(), StringArgumentType.getString(ctx, "amount"), 2, ctx.getSource().getRegistryManager());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }


    public static CalcMessageBuilder execute(Entity player, Recipe item, String amount, int steps, DynamicRegistryManager registryManager) {

        var is = item.getIngredients();
        var outputSize = item.getResult(registryManager).getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);
//        Map<String, Integer> ingredients = new HashMap<String, Integer>();
//        Map<String, ItemStack> ingredientsStacks = new HashMap<String, ItemStack>();
//        for (Object i : is) {
//            Ingredient ingredient = (Ingredient) i;
//
//
//            if (ingredient.getMatchingStacks().length > 0) {
//                if (ingredients.containsKey(ingredient.getMatchingStacks()[0].getName().getString())) {
//                    CalcMod.LOGGER.info("hello");
//                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()) + a );
//                } else {
//                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), a);
//                    ingredientsStacks.put(ingredient.getMatchingStacks()[0].getName().getString(), ingredient.getMatchingStacks()[0]);
//                    CalcMod.LOGGER.info(String.valueOf(ingredient.getMatchingStacks()[0].getCount()));
//
//                }
//
//            //ingredients.merge(ingredient.getMatchingStacks()[0], a, Integer::sum);
//            }
//        }
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player.getWorld().getRecipeManager(), registryManager, is, a, steps);
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), item.getResult(registryManager).getName().getString()}, new String[] {});
        
        for (Map.Entry<String, Map.Entry<ItemStack, Integer>> entry : ingredients.entrySet()) {
            String key = entry.getKey();
            ItemStack value = entry.getValue().getKey();
            int stackSize = value.getMaxCount();
            double sb = Math.floor(entry.getValue().getValue()/(stackSize*27));
            String sbString = nf.format(sb);
            int remainder = entry.getValue().getValue() % (stackSize*27);
            double stacks = Math.floor(remainder/stackSize);
            String stacksString = nf.format(stacks);
            remainder = remainder % stackSize;
            String items = nf.format(remainder);
            if (sb > 0) {
                messageBuilder.addString(key+": ");
                messageBuilder.addResult("SBs: "+sbString + ", Stacks: "+stacksString+", Items: "+items+"\n");
            } else if (stacks > 0) {
                messageBuilder.addString(key + ": " );
                messageBuilder.addResult("Stacks: "+stacksString+", Items: "+items+"\n");
            } else {
                messageBuilder.addString(key + ": " );
                messageBuilder.addResult("Items: "+items+"\n");
            }
        }

   //     message.set(0, "Ingredients needed for crafting "+nf.format(inputAmount)+" "+item.getOutput(registryManager).getName().getString()+"s: \n"+message.get(0));
        
        return messageBuilder;
    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(RecipeManager manager, DynamicRegistryManager registryManager, DefaultedList<Ingredient> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
    //    CalcMod.LOGGER.info("Step"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
        for (Ingredient ingredient : is) {

       //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());

            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
            if (ingredient.getMatchingStacks().length > 0) {
                if (ingredients.containsKey(ingredient.getMatchingStacks()[0].getName().getString())) {
                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), Map.entry(ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()).getKey(), ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()).getValue()+amount_needed));
                } else {
           //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), Map.entry(ingredient.getMatchingStacks()[0], amount_needed));
                }
          //      CalcMod.LOGGER.info("Step4"+ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()).getValue());

                //ingredients.merge(ingredient.getMatchingStacks()[0], a, Integer::sum);
            }
        }
        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();

        for (Map.Entry<ItemStack, Integer> ingredient : ingredients.values()) {
            if (steps == 1) {
               // CalcMod.LOGGER.info(is.get(0).getMatchingStacks()[0].getName().getString());
               return  ingredients;
            } else {
               // CalcMod.LOGGER.info("new");
                //     CalcMod.LOGGER.info(manager.get(ingredient.getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
                Optional<Identifier> ing_id = Optional.ofNullable(ingredient.getKey().getRegistryEntry().getKey().get().getValue());
                CalcMod.LOGGER.info(ing_id.get().getPath());
                if (ing_id.get().getPath() .contains("ingot")) {

                    Optional<Identifier> finalIng_id = ing_id;
                  //  CalcMod.LOGGER.info(finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath() .split("_")[0] + "_block");
                    ing_id = manager.keys().filter(x ->
                        Objects.equals(x.getPath(), finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath() .split("_")[0] + "_block")
                ).findFirst();
                }
                if (manager.get(ing_id.get()).isPresent()) {
                    Recipe<?> recipe = manager.get(ing_id.get()).get().value();
                    DefaultedList<Ingredient> sis = recipe.getIngredients();
             //       CalcMod.LOGGER.info(String.valueOf(ingredient.getValue()));
                //    CalcMod.LOGGER.info(String.valueOf(recipe.getResult(registryManager).getCount()));
              //      CalcMod.LOGGER.info(String.valueOf((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()));
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(manager, registryManager, sis, (int) Math.ceil((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()), steps - 1);
               //     CalcMod.LOGGER.info(recipe.getResult(registryManager).getName().getString());
               //     ingredients.remove(recipe.getResult(registryManager).getName().getString());
                    for (String item : sub_ingredients.keySet()) {
                        if (ex_ingredients.containsKey(item)) {
                            ex_ingredients.put(item, Map.entry(ingredients.get(item).getKey(), ingredients.get(item).getValue() + sub_ingredients.get(item).getValue()));
                        } else {
                            ex_ingredients.put(item, Map.entry(sub_ingredients.get(item).getKey(), sub_ingredients.get(item).getValue()));
                        }
                    }
                } else {
                    ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
                }

            }
        }

        return ex_ingredients;
    }

    public static String helpMessage = """
            §b§LCraft:§r§f
                    Given a desired item and the quantity to be crafted §7§o(can be in expression form)§r§f, returns the amounts of the items needed to craft the amount of the desired item.
                    Depth specifies how many levels of recursive crafting to perform on the recipe. Default depth is 1.\s
                        §eUsage: /calc craft <item> <amount>§f
                        §eUsage: /calc craft <item> <depth> <amount>§f
            """;
    
}
