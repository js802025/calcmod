package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.jsa2025.calcmod.commands.arguments.CIdentifierArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder; 

import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.arguments.CRecipeSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items; 
import net.minecraft.recipe.*;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.DynamicRegistryManager; 
import net.minecraft.server.command.CommandManager; 
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text; 
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildClientNode() {
        LiteralArgumentBuilder<FabricClientCommandSource> craftLiteral = ClientCommandManager.literal("craft");

        RequiredArgumentBuilder<FabricClientCommandSource, Identifier> itemArgument = ClientCommandManager.argument("item", CIdentifierArgumentType.identifier())
            .suggests(new CRecipeSuggestionProvider())
            .executes(ctx -> { 
                Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item");
                String itemName = itemId.toString();
                if (!itemId.toString().contains(":")) {
                    ctx.getSource().sendError(Text.literal("Invalid item format: '"+itemId.toString()+"'. Please use a namespaced ID like 'minecraft:"+itemId.toString()+"'."));
                    return 0; 
                }
                Item item = Registries.ITEM.get(itemId);
                if (item == Items.AIR && !Objects.equals(itemId.toString(), "minecraft:air")) {
                    ctx.getSource().sendError(Text.literal("Invalid item: " + itemName));
                    return 0;
                }
                ItemStack targetItemStack = new ItemStack(item);
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), targetItemStack, "1", 1, ctx.getSource().getRegistryManager());
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            });

        itemArgument.then(ClientCommandManager.argument("amount", StringArgumentType.string()) 
            .executes(ctx -> {
                Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item");
                String itemName = itemId.toString();
                if (!itemId.toString().contains(":")) {
                    ctx.getSource().sendError(Text.literal("Invalid item format: '"+itemId.toString()+"'. Please use a namespaced ID like 'minecraft:"+itemId.toString()+"'."));
                    return 0; 
                }
                Item item = Registries.ITEM.get(itemId);
                if (item == Items.AIR && !Objects.equals(itemId.toString(), "minecraft:air")) {
                    ctx.getSource().sendError(Text.literal("Invalid item: " + itemName));
                    return 0;
                }
                ItemStack targetItemStack = new ItemStack(item);
                String amountStr = StringArgumentType.getString(ctx, "amount");
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), targetItemStack, amountStr, 1, ctx.getSource().getRegistryManager());
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            }));

        itemArgument.then(ClientCommandManager.literal("depth")
            .then(ClientCommandManager.argument("level", IntegerArgumentType.integer())
                .then(ClientCommandManager.argument("amount_with_depth", StringArgumentType.greedyString()) 
                    .executes(ctx -> {
                        Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item");
                        String itemName = itemId.toString();
                        if (!itemId.toString().contains(":")) {
                            ctx.getSource().sendError(Text.literal("Invalid item format: '"+itemId.toString()+"'. Please use a namespaced ID like 'minecraft:"+itemId.toString()+"'."));
                            return 0; 
                        }
                        Item item = Registries.ITEM.get(itemId);
                        if (item == Items.AIR && !Objects.equals(itemId.toString(), "minecraft:air")) {
                            ctx.getSource().sendError(Text.literal("Invalid item: " + itemName));
                            return 0;
                        }
                        ItemStack targetItemStack = new ItemStack(item);
                        int depth = IntegerArgumentType.getInteger(ctx, "level");
                        String amountStr = StringArgumentType.getString(ctx, "amount_with_depth");
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), targetItemStack, amountStr, depth, ctx.getSource().getRegistryManager());
                        CalcCommand.sendMessage(ctx.getSource(), message);
                        return 1;
                    }))));
        
        craftLiteral.then(itemArgument);
        
        craftLiteral.then(ClientCommandManager.literal("help")
            .executes(ctx -> {
                CalcMessageBuilder message = Help.execute("craft");
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })
        );

        return craftLiteral; 
    }

    
    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        var craftLiteral = CommandManager.literal("craft"); 
        
        craftLiteral.then(CommandManager.argument("item", StringArgumentType.string()).suggests(new RecipeSuggestionProvider())
            .executes(ctx -> { 
                String itemName = StringArgumentType.getString(ctx, "item");
                Identifier itemId = Identifier.of(itemName);
                Item item = Registries.ITEM.get(itemId);
                 if (item == Items.AIR && !Objects.equals(itemId.toString(), "minecraft:air")) {
                    ctx.getSource().sendError(Text.literal("Invalid item: " + itemName));
                    return 0;
                }
                ItemStack itemStack = new ItemStack(item);
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), itemStack, "1", 2, ctx.getSource().getRegistryManager());
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            })
            .then(CommandManager.argument("amount", StringArgumentType.greedyString())
            .executes((ctx) -> { 
                String itemName = StringArgumentType.getString(ctx, "item");
                Identifier itemId = Identifier.of(itemName);
                Item item = Registries.ITEM.get(itemId);
                if (item == Items.AIR && !Objects.equals(itemId.toString(), "minecraft:air")) {
                    ctx.getSource().sendError(Text.literal("Invalid item: " + itemName));
                    return 0;
                }
                ItemStack itemStack = new ItemStack(item);
                CalcMessageBuilder message = execute(ctx.getSource().getEntity(), itemStack, StringArgumentType.getString(ctx, "amount"), 2, ctx.getSource().getRegistryManager());
                CalcCommand.sendMessageServer(ctx.getSource(), message);
                return 1;
            }))
        ); 
        
        craftLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));
        
        return craftLiteral; 
    }

    public static CalcMessageBuilder execute(Entity player, ItemStack targetItemStack, String amount, int steps, DynamicRegistryManager registryManager) {
        CalcMod.LOGGER.info("Executing craft with ItemStack: " + targetItemStack.getName().getString() + ", Amount: " + amount + ", Steps: " + steps);
        
        if (targetItemStack.getItem() == Items.AIR) { 
            return new CalcMessageBuilder().addString("Invalid item: minecraft:air. Cannot craft air.");
        }

        RecipeManager recipeManager = player.getWorld().getRecipeManager();
        Recipe<?> foundRecipe = null;

        for (RecipeEntry<?> recipeEntry : recipeManager.values()) {
            Recipe<?> currentRecipe = recipeEntry.value();
            RecipeSerializer<?> serializer = currentRecipe.getSerializer();
            if ((serializer == RecipeSerializer.SHAPED || serializer == RecipeSerializer.SHAPELESS) && 
                currentRecipe.getResult(registryManager) != null && 
                currentRecipe.getResult(registryManager).getItem() == targetItemStack.getItem()) {
                foundRecipe = currentRecipe;
                break; 
            }
        }

        if (foundRecipe == null) {
            return new CalcMessageBuilder().addString("No recipe found for " + targetItemStack.getName().getString());
        }

        DefaultedList<Ingredient> ingredientsList = foundRecipe.getIngredients();
        if (ingredientsList.isEmpty() && targetItemStack.getMaxCount() == 64) { 
             return new CalcMessageBuilder().addString(targetItemStack.getName().getString() + " is a base item and cannot be crafted further with this command's logic.");
        }

        int outputSize = foundRecipe.getResult(registryManager).getCount();
        if (outputSize == 0) { 
            return new CalcMessageBuilder().addString("Recipe for " + targetItemStack.getName().getString() + " has an output of 0, cannot calculate.");
        }
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int baseCrafts = (int) Math.ceil(inputAmount / outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(recipeManager, registryManager, ingredientsList, baseCrafts, steps);
        
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), targetItemStack.getName().getString()}, new String[] {});
        
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

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(RecipeManager manager, DynamicRegistryManager registryManager, DefaultedList<Ingredient> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
        for (Ingredient ingredient : is) {
            if (ingredient.getMatchingStacks().length > 0) {
                if (ingredients.containsKey(ingredient.getMatchingStacks()[0].getName().getString())) {
                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), Map.entry(ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()).getKey(), ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()).getValue()+amount_needed));
                } else {
                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), Map.entry(ingredient.getMatchingStacks()[0], amount_needed));
                }
            }
        }
        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();

        for (Map.Entry<ItemStack, Integer> ingredient : ingredients.values()) {
            if (steps == 1) {
               ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
            } else {
                Optional<Identifier> ing_id = Optional.ofNullable(ingredient.getKey().getRegistryEntry().getKey().get().getValue());
                if (ing_id.isEmpty()) {
                    ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
                    continue;
                }

                CalcMod.LOGGER.info("Sub-crafting for: "+ing_id.get().getPath()+" current depth: "+steps);
                if (ing_id.get().getPath().contains("ingot")) { 
                    Optional<Identifier> finalIng_id = ing_id;
                    ing_id = manager.keys().filter(x ->
                        Objects.equals(x.getPath(), finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath().split("_")[0] + "_block")
                    ).findFirst();
                }

                if (ing_id.isPresent() && manager.get(ing_id.get()).isPresent()) {
                    Recipe<?> recipe = manager.get(ing_id.get()).get().value();
                    RecipeSerializer<?> subRecipeSerializer = recipe.getSerializer();
                    if (!(subRecipeSerializer == RecipeSerializer.SHAPED || subRecipeSerializer == RecipeSerializer.SHAPELESS)) {
                        ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
                        continue;
                    }
                    DefaultedList<Ingredient> sis = recipe.getIngredients();
                    if (sis.isEmpty()) { 
                         ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
                         continue;
                    }
                    int recipeOutputCount = recipe.getResult(registryManager).getCount();
                    if (recipeOutputCount == 0) recipeOutputCount = 1; 
                    
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(manager, registryManager, sis, (int) Math.ceil((double) ingredient.getValue() / recipeOutputCount), steps - 1);
                    for (String item_name : sub_ingredients.keySet()) { 
                        if (ex_ingredients.containsKey(item_name)) {
                            Map.Entry<ItemStack, Integer> existingEntry = ex_ingredients.get(item_name);
                            Map.Entry<ItemStack, Integer> subEntry = sub_ingredients.get(item_name);
                            ex_ingredients.put(item_name, Map.entry(existingEntry.getKey(), existingEntry.getValue() + subEntry.getValue()));
                        } else {
                            ex_ingredients.put(item_name, sub_ingredients.get(item_name));
                        }
                    }
                } else { 
                    ex_ingredients.put(ingredient.getKey().getName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
                }
            }
        }
        if (steps == 1) return ingredients; 
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
