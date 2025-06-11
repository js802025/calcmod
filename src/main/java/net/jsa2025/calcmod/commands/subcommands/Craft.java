package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import net.jsa2025.calcmod.commands.arguments.ContainerSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    
    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("craft").then(Commands.argument("item", ArgumentTypes.namespacedKey()).suggests(new RecipeSuggestionProvider())
                        .then(Commands.literal("depth").then( Commands.argument("level", IntegerArgumentType.integer())
        .then(Commands.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), ctx.getSource().getExecutor().getServer().getRecipe(ctx.getArgument("item", NamespacedKey.class)), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"));
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        ).then(Commands.argument("amount", StringArgumentType.greedyString())
                        .executes((ctx) -> {
                      //      CalcMod.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(ctx.getArgument("item", ItemStack.class).effectiveName()));
                            CalcMessageBuilder message = execute(ctx.getSource().getExecutor(), ctx.getSource().getExecutor().getServer().getRecipe(ctx.getArgument("item", NamespacedKey.class)), StringArgumentType.getString(ctx, "amount"), 1);
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        })))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    
//    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command, CommandRegistryAccess registry) {
//        command.then(CommandManager.literal("craft").then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new RecipeSuggestionProvider())
//                                .then(CommandManager.literal("depth").then( CommandManager.argument("level", IntegerArgumentType.integer())
//                                        .then(CommandManager.argument("amount", StringArgumentType.greedyString())
//                                                .executes((ctx) -> {
//                                                    CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgumentServer(ctx, "item"), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().getRegistryManager());
//                                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
//                                                    return 1;
//                                                })))
//                                ).then(CommandManager.argument("amount", StringArgumentType.greedyString())
//                                        .executes((ctx) -> {
//                                            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgumentServer(ctx, "item"), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().getRegistryManager());
//                                            CalcCommand.sendMessageServer(ctx.getSource(), message);
//                                            return 1;
//                                        })))
//                        .then(CommandManager.literal("help").executes(ctx -> {
//                            CalcMessageBuilder message = Help.execute("craft");
//                            CalcCommand.sendMessageServer(ctx.getSource(), message);
//                            return 1;
//                        })));
//        return command;
//    }

    public static CalcMessageBuilder execute(Entity player, Recipe recipe, String amount, int steps) {
        List<ItemStack> is;
        if (recipe.getClass().getName().contains("Shaped")) {
            is = ((ShapedRecipe) recipe).getChoiceMap().values().stream().map(i -> i.getItemStack()).toList();
        } else {
            is = ((ShapelessRecipe) recipe).getChoiceList().stream().map(i -> i.getItemStack()).toList();
        }
        var outputSize = recipe.getResult().getAmount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player, Optional.of(is), a, steps);
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), PlainTextComponentSerializer.plainText().serialize(recipe.getResult().effectiveName())}, new String[] {});
        
        for (Map.Entry<String, Map.Entry<ItemStack, Integer>> entry : ingredients.entrySet()) {
            String key = entry.getKey();
            ItemStack value = entry.getValue().getKey();
            int stackSize = value.getMaxStackSize();
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

        return messageBuilder;
    }


//    public static CalcMessageBuilder execute(ServerPlayerEntity player, Recipe item, String amount, int steps, DynamicRegistryManager registryManager) {
//        var is = item.getIngredientPlacement().getIngredients();
//        var outputSize = ((RecipeDisplay)item.getDisplays().get(0)).result().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getCount();
//        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
//        int a = (int) Math.ceil(inputAmount/outputSize);
//
//        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player, player.getServer().getRecipeManager(), registryManager, Optional.ofNullable(is), a, steps);
//        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
//                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), ((RecipeDisplay) item.getDisplays().get(0)).result().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getName().getString()}, new String[] {});
//
//        for (Map.Entry<String, Map.Entry<ItemStack, Integer>> entry : ingredients.entrySet()) {
//            String key = entry.getKey();
//            ItemStack value = entry.getValue().getKey();
//            int stackSize = value.getMaxCount();
//            double sb = Math.floor(entry.getValue().getValue()/(stackSize*27));
//            String sbString = nf.format(sb);
//            int remainder = entry.getValue().getValue() % (stackSize*27);
//            double stacks = Math.floor(remainder/stackSize);
//            String stacksString = nf.format(stacks);
//            remainder = remainder % stackSize;
//            String items = nf.format(remainder);
//            if (sb > 0) {
//                messageBuilder.addString(key+": ");
//                messageBuilder.addResult("SBs: "+sbString + ", Stacks: "+stacksString+", Items: "+items+"\n");
//            } else if (stacks > 0) {
//                messageBuilder.addString(key + ": " );
//                messageBuilder.addResult("Stacks: "+stacksString+", Items: "+items+"\n");
//            } else {
//                messageBuilder.addString(key + ": " );
//                messageBuilder.addResult("Items: "+items+"\n");
//            }
//        }
//
//        return messageBuilder;
//    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(Entity player, Optional<List<ItemStack>> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
    //    CalcMod.LOGGER.info("Step"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
        for (ItemStack ingredient : is.get()) {
       //    ingredient.getMatchingItems().get(0).getKey().get().toString()

       //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());

            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
          //  if (!ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).isEmpty()) {
                if (ingredients.containsKey(PlainTextComponentSerializer.plainText().serialize(ingredient.effectiveName()))) {
                    ingredients.put(PlainTextComponentSerializer.plainText().serialize(ingredient.effectiveName()), Map.entry(ingredient, ingredients.get(PlainTextComponentSerializer.plainText().serialize(ingredient.effectiveName())).getValue()+amount_needed));
                } else {
           //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
                    ingredients.put(PlainTextComponentSerializer.plainText().serialize(ingredient.effectiveName()), Map.entry(ingredient, amount_needed));
                }
          //      CalcMod.LOGGER.info("Step4"+ingredients.get(ingredient.getMatchingItems().get(0).getIdAsString()).getValue());

            //}
        }
        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();

        for (Map.Entry<ItemStack, Integer> ingredient : ingredients.values()) {
            if (steps == 1) {
               // CalcMod.LOGGER.info(is.get(0).getMatchingStacks()[0].getName().getString());
               return  ingredients;
            } else {
               // CalcMod.LOGGER.info("new");
                //     CalcMod.LOGGER.info(manager.get(ingredient.getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
                Optional<ItemStack> ing_id = Optional.ofNullable(ingredient.getKey());


                if (ing_id.isPresent()) {
                  //  CalcMod.LOGGER.info("MATCH FOUND for "+ing_id.get().getPath()+": "+recipeResultCollection.get().getAllRecipes().get(0).display().result().getStacks(SlotDisplayContexts.createParameters(player.getWorld())).get(0).getRegistryEntry().getIdAsString());

//                    RecipeDisplayEntry recipeDisplayEntry = recipeResultCollection.get().getAllRecipes().stream().filter(i -> i.display().result().getStacks(SlotDisplayContexts.createParameters(player.getWorld())).get(0).getRegistryEntry().getIdAsString().contains(ing_id.get().getPath())).findFirst().get();
////                    Recipe<?> recipe = manager.get(ing_id.get()).get().value();
                    Optional<List<ItemStack>> sis;
                    Recipe recipe = player.getServer().getRecipesFor(ing_id.get()).get(0);
                    if (recipe.getClass().isInstance(ShapedRecipe.class)) {
                        sis = Optional.of(((ShapedRecipe) recipe).getChoiceMap().values().stream().map(i -> i.getItemStack()).toList());
                    } else {
                        sis = Optional.of(((ShapelessRecipe) recipe).getChoiceList().stream().map(i -> i.getItemStack()).toList());
                    }
                 //   CalcMod.LOGGER.info(String.valueOf(ingredient.getValue()));
                //    CalcMod.LOGGER.info(String.valueOf(recipe.getResult(registryManager).getCount()));
              //      CalcMod.LOGGER.info(String.valueOf((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()));
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(player, sis, (int) Math.ceil((double) ingredient.getValue() / (double) recipe.getResult().getAmount()), steps - 1);
               //     CalcMod.LOGGER.info(recipe.getResult(registryManager).getName().getString());
                    for (String item : sub_ingredients.keySet()) {
                        if (ex_ingredients.containsKey(item)) {
                            ex_ingredients.put(item, Map.entry(ingredients.get(item).getKey(), ingredients.get(item).getValue() + sub_ingredients.get(item).getValue()));
                        } else {
                            ex_ingredients.put(item, Map.entry(sub_ingredients.get(item).getKey(), sub_ingredients.get(item).getValue()));
                        }
                    }
                } else {
                    ex_ingredients.put(PlainTextComponentSerializer.plainText().serialize(ingredient.getKey().effectiveName()), Map.entry(ingredient.getKey(), ingredient.getValue()));
                }
             //   return ingredients;
            }
        }

        return ex_ingredients;
    }

//    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(PlayerEntity player, ServerRecipeManager manager, DynamicRegistryManager registryManager, Optional<List<Ingredient>> is, int amount_needed, int steps) {
//        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
//        //    CalcMod.LOGGER.info("Step"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
//        for (Ingredient ingredient : is.get()) {
//            //    ingredient.getMatchingItems().get(0).getKey().get().toString()
//
//            //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
//
//            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
//            if (!ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).isEmpty()) {
//                if (ingredients.containsKey(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getName().getString())) {
//                    ingredients.put(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getName().getString(), Map.entry(ingredients.get(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getName().getString()).getKey(), ingredients.get(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getName().getString()).getValue()+amount_needed));
//                } else {
//                    //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
//                    ingredients.put(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getName().getString(), Map.entry(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getWorld())), amount_needed));
//                }
//                //      CalcMod.LOGGER.info("Step4"+ingredients.get(ingredient.getMatchingItems().get(0).getIdAsString()).getValue());
//
//            }
//        }
//        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
//
//        for (Map.Entry<ItemStack, Integer> ingredient : ingredients.values()) {
//            if (steps == 1) {
//                // CalcMod.LOGGER.info(is.get(0).getMatchingStacks()[0].getName().getString());
//                return  ingredients;
//            } else {
//                // CalcMod.LOGGER.info("new");
//                //     CalcMod.LOGGER.info(manager.get(ingredient.getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
//                Optional<Identifier> ing_id = Optional.ofNullable(ingredient.getKey().getRegistryEntry().getKey().get().getValue());
//             //   CalcMod.LOGGER.info(ing_id.get().getPath());
//                if (ing_id.get().getPath() .contains("ingot")) {
//
//                    Optional<Identifier> finalIng_id = ing_id;
//                    //  CalcMod.LOGGER.info(finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath() .split("_")[0] + "_block");
//                    ing_id = Optional.ofNullable(manager.values().stream().filter(x ->
//                            Objects.equals(x.id().getValue().getPath(), finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath().split("_")[0] + "_block")
//                    ).findFirst().get().id().getValue());
//                }
//                Optional<Identifier> finalIng_id1 = ing_id;
//                if (manager.values().stream().filter(val -> Objects.equals(finalIng_id1.get(), val.id().getValue())).count() > 0) {
//                    Recipe<?> recipe = manager.values().stream().filter(val -> Objects.equals(finalIng_id1.get(), val.id().getValue())).findFirst().get().value();
//                    List<Ingredient> sis = recipe.getIngredientPlacement().getIngredients();
//                    //       CalcMod.LOGGER.info(String.valueOf(ingredient.getValue()));
//                    //    CalcMod.LOGGER.info(String.valueOf(recipe.getResult(registryManager).getCount()));
//                    //      CalcMod.LOGGER.info(String.valueOf((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()));
//                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(player, manager, registryManager, Optional.ofNullable(sis), (int) Math.ceil((double) ingredient.getValue() / (double) recipe.getDisplays().get(0).result().getFirst(SlotDisplayContexts.createParameters(player.getWorld())).getCount()), steps - 1);
//                    //     CalcMod.LOGGER.info(recipe.getResult(registryManager).getName().getString());
//                    //     ingredients.remove(recipe.getResult(registryManager).getName().getString());
//                    for (String item : sub_ingredients.keySet()) {
//                        if (ex_ingredients.containsKey(item)) {
//                            ex_ingredients.put(item, Map.entry(ingredients.get(item).getKey(), ingredients.get(item).getValue() + sub_ingredients.get(item).getValue()));
//                        } else {
//                            ex_ingredients.put(item, Map.entry(sub_ingredients.get(item).getKey(), sub_ingredients.get(item).getValue()));
//                        }
//                    }
//                } else {
//                    ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
//                }
//            }
//        }
//
//        return ex_ingredients;
//    }

    public static String helpMessage = """
            §b§LCraft:§r§f
                    Given a desired item and the quantity to be crafted §7§o(can be in expression form)§r§f, returns the amounts of the items needed to craft the amount of the desired item.
                    Depth specifies how many levels of recursive crafting to perform on the recipe. Default depth is 1.\s
                        §eUsage: /calc craft <item> <amount>§f
                        §eUsage: /calc craft <item> <depth> <amount>§f
            """;
    
}
