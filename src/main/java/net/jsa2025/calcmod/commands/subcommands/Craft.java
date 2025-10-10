package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.arguments.CIdentifierArgumentType;
import net.jsa2025.calcmod.commands.arguments.CRecipeSuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;

import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    
    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command, CommandRegistryAccess registry) {
        command
        .then(ClientCommandManager.literal("craft").then(ClientCommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new CRecipeSuggestionProvider())
                        .then(ClientCommandManager.literal("depth").then( ClientCommandManager.argument("level", IntegerArgumentType.integer())
        .then(ClientCommandManager.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgument(ctx, "item"), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().getRegistryManager());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        ).then(ClientCommandManager.argument("amount", StringArgumentType.greedyString())
                        .executes((ctx) -> {
                            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgument(ctx, "item"), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().getRegistryManager());
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
        command.then(CommandManager.literal("craft").then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new RecipeSuggestionProvider())
                                .then(CommandManager.literal("depth").then( CommandManager.argument("level", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("amount", StringArgumentType.greedyString())
                                                .executes((ctx) -> {
                                                    CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgumentServer(ctx, "item"), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().getRegistryManager());
                                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                                    return 1;
                                                })))
                                ).then(CommandManager.argument("amount", StringArgumentType.greedyString())
                                        .executes((ctx) -> {
                                            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgumentServer(ctx, "item"), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().getRegistryManager());
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

    @Environment(EnvType.CLIENT)
    public static CalcMessageBuilder execute(ClientPlayerEntity player, RecipeDisplayEntry item, String amount, int steps, DynamicRegistryManager registryManager) {
        var is = item.craftingRequirements();
        var outputSize = item.display().result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player, player.getRecipeBook(), registryManager, is, a, steps);
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), item.display().result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()}, new String[] {});
        
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

        return messageBuilder;
    }


    public static CalcMessageBuilder execute(ServerPlayerEntity player, Recipe item, String amount, int steps, DynamicRegistryManager registryManager) {
        var is = item.getIngredientPlacement().getIngredients();
        var outputSize = ((RecipeDisplay)item.getDisplays().get(0)).result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player, player.getEntityWorld().getServer().getRecipeManager(), registryManager, Optional.ofNullable(is), a, steps);
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), ((RecipeDisplay) item.getDisplays().get(0)).result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()}, new String[] {});

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

        return messageBuilder;
    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(PlayerEntity player, ClientRecipeBook book, DynamicRegistryManager registryManager, Optional<List<Ingredient>> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
    //    CalcMod.LOGGER.info("Step"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
        for (Ingredient ingredient : is.get()) {
       //    ingredient.getMatchingItems().get(0).getKey().get().toString()

       //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());

            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
            if (!ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).isEmpty()) {
                if (ingredients.containsKey(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString())) {
                    ingredients.put(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString(), Map.entry(ingredients.get(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()).getKey(), ingredients.get(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()).getValue()+amount_needed));
                } else {
           //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
                    ingredients.put(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString(), Map.entry(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())), amount_needed));
                }
          //      CalcMod.LOGGER.info("Step4"+ingredients.get(ingredient.getMatchingItems().get(0).getIdAsString()).getValue());

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


                Optional<RecipeResultCollection> recipeResultCollection = book.getOrderedResults().stream().filter(x ->
                        x.getAllRecipes().stream().anyMatch(i -> {
                                    //    Logger.getLogger("calcmod").info(i.display().result().getStacks(SlotDisplayContexts.createParameters(player.getEntityWorld())).get(0).getRegistryEntry().getIdAsString() + " "+ing_id.get().getPath());
                                    return i.display().result().getStacks(SlotDisplayContexts.createParameters(player.getEntityWorld())).get(0).getRegistryEntry().getIdAsString().contains(ing_id.get().getPath());
                                }
                        )).findFirst();
                if (recipeResultCollection.isPresent()) {
                  //  CalcMod.LOGGER.info("MATCH FOUND for "+ing_id.get().getPath()+": "+recipeResultCollection.get().getAllRecipes().get(0).display().result().getStacks(SlotDisplayContexts.createParameters(player.getEntityWorld())).get(0).getRegistryEntry().getIdAsString());

                    RecipeDisplayEntry recipeDisplayEntry = recipeResultCollection.get().getAllRecipes().stream().filter(i -> i.display().result().getStacks(SlotDisplayContexts.createParameters(player.getEntityWorld())).get(0).getRegistryEntry().getIdAsString().contains(ing_id.get().getPath())).findFirst().get();
//                    Recipe<?> recipe = manager.get(ing_id.get()).get().value();
                    Optional<List<Ingredient>> sis = recipeDisplayEntry.craftingRequirements();
                 //   CalcMod.LOGGER.info(String.valueOf(ingredient.getValue()));
                //    CalcMod.LOGGER.info(String.valueOf(recipe.getResult(registryManager).getCount()));
              //      CalcMod.LOGGER.info(String.valueOf((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()));
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(player, book, registryManager, sis, (int) Math.ceil((double) ingredient.getValue() / (double) recipeDisplayEntry.display().result().getStacks(SlotDisplayContexts.createParameters(player.getEntityWorld())).get(0).getCount()), steps - 1);
               //     CalcMod.LOGGER.info(recipe.getResult(registryManager).getName().getString());
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
             //   return ingredients;
            }
        }

        return ex_ingredients;
    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(PlayerEntity player, ServerRecipeManager manager, DynamicRegistryManager registryManager, Optional<List<Ingredient>> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
        //    CalcMod.LOGGER.info("Step"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
        for (Ingredient ingredient : is.get()) {
            //    ingredient.getMatchingItems().get(0).getKey().get().toString()

            //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());

            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
            if (!ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).isEmpty()) {
                if (ingredients.containsKey(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString())) {
                    ingredients.put(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString(), Map.entry(ingredients.get(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()).getKey(), ingredients.get(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()).getValue()+amount_needed));
                } else {
                    //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
                    ingredients.put(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString(), Map.entry(ingredient.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())), amount_needed));
                }
                //      CalcMod.LOGGER.info("Step4"+ingredients.get(ingredient.getMatchingItems().get(0).getIdAsString()).getValue());

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
             //   CalcMod.LOGGER.info(ing_id.get().getPath());
                if (ing_id.get().getPath() .contains("ingot")) {

                    Optional<Identifier> finalIng_id = ing_id;
                    //  CalcMod.LOGGER.info(finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath() .split("_")[0] + "_block");
                    ing_id = Optional.ofNullable(manager.values().stream().filter(x ->
                            Objects.equals(x.id().getValue().getPath(), finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath().split("_")[0] + "_block")
                    ).findFirst().get().id().getValue());
                }
                Optional<Identifier> finalIng_id1 = ing_id;
                if (manager.values().stream().filter(val -> Objects.equals(finalIng_id1.get(), val.id().getValue())).count() > 0) {
                    Recipe<?> recipe = manager.values().stream().filter(val -> Objects.equals(finalIng_id1.get(), val.id().getValue())).findFirst().get().value();
                    List<Ingredient> sis = recipe.getIngredientPlacement().getIngredients();
                    //       CalcMod.LOGGER.info(String.valueOf(ingredient.getValue()));
                    //    CalcMod.LOGGER.info(String.valueOf(recipe.getResult(registryManager).getCount()));
                    //      CalcMod.LOGGER.info(String.valueOf((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()));
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(player, manager, registryManager, Optional.ofNullable(sis), (int) Math.ceil((double) ingredient.getValue() / (double) recipe.getDisplays().get(0).result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getCount()), steps - 1);
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
