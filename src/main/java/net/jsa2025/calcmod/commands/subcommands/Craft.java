package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.jsa2025.calcmod.commands.arguments.CIdentifierArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

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

        craftLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        });

        craftLiteral.then(ClientCommandManager.literal("help")
            .executes(ctx -> {
                CalcMessageBuilder message = Help.execute("craft");
                CalcCommand.sendMessage(ctx.getSource(), message);
                return 1;
            })
        );

        craftLiteral.then(ClientCommandManager.literal("item")
            .then(ClientCommandManager.argument("item_id", CIdentifierArgumentType.identifier())
                .suggests(new CRecipeSuggestionProvider())
                .executes(ctx -> { // /calc craft item <item_id>
                    Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item_id");
                    if (!ensureNamespace(itemId.toString(), ctx.getSource())) return 0;
                    Item item = Registries.ITEM.get(itemId);
                    if (!validateItem(item, itemId.toString(), ctx.getSource())) return 0;
                    
                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), "1", 1, ctx.getSource().getRegistryManager());
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                })
                .then(ClientCommandManager.argument("amount", StringArgumentType.string())
                    .executes(ctx -> { // /calc craft item <item_id> <amount>
                        Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item_id");
                        if (!ensureNamespace(itemId.toString(), ctx.getSource())) return 0;
                        Item item = Registries.ITEM.get(itemId);
                        if (!validateItem(item, itemId.toString(), ctx.getSource())) return 0;
                        String amountStr = StringArgumentType.getString(ctx, "amount");
                        
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), amountStr, 1, ctx.getSource().getRegistryManager());
                        CalcCommand.sendMessage(ctx.getSource(), message);
                        return 1;
                    }))));

        craftLiteral.then(ClientCommandManager.literal("item_depth")
            .then(ClientCommandManager.argument("item_id", CIdentifierArgumentType.identifier())
                .suggests(new CRecipeSuggestionProvider())
                .then(ClientCommandManager.argument("depth_level", IntegerArgumentType.integer())
                    .executes(ctx -> { // /calc craft item_depth <item_id> <depth_level>
                        Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item_id");
                        if (!ensureNamespace(itemId.toString(), ctx.getSource())) return 0;
                        Item item = Registries.ITEM.get(itemId);
                        if (!validateItem(item, itemId.toString(), ctx.getSource())) return 0;
                        int depth = IntegerArgumentType.getInteger(ctx, "depth_level");
                        
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), "1", depth, ctx.getSource().getRegistryManager());
                        CalcCommand.sendMessage(ctx.getSource(), message);
                        return 1;
                    })
                    .then(ClientCommandManager.argument("amount_with_depth", StringArgumentType.string())
                        .executes(ctx -> { // /calc craft item_depth <item_id> <depth_level> <amount_with_depth>
                            Identifier itemId = CIdentifierArgumentType.getIdentifier(ctx, "item_id");
                            if (!ensureNamespace(itemId.toString(), ctx.getSource())) return 0;
                            Item item = Registries.ITEM.get(itemId);
                            if (!validateItem(item, itemId.toString(), ctx.getSource())) return 0;
                            int depth = IntegerArgumentType.getInteger(ctx, "depth_level");
                            String amountStr = StringArgumentType.getString(ctx, "amount_with_depth");

                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), amountStr, depth, ctx.getSource().getRegistryManager());
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        })))));
        return craftLiteral; 
    }

    
    public static LiteralArgumentBuilder<ServerCommandSource> buildServerNode() {
        LiteralArgumentBuilder<ServerCommandSource> craftLiteral = CommandManager.literal("craft"); 
        
        craftLiteral.executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        });

        craftLiteral.then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        }));

        craftLiteral.then(CommandManager.literal("item")
            .then(CommandManager.argument("item_id_str", StringArgumentType.string()) 
                .suggests(new RecipeSuggestionProvider())
                .executes(ctx -> { // /calc craft item <item_id_str>
                    Identifier itemId = Identifier.of(StringArgumentType.getString(ctx, "item_id_str"));
                    Item item = Registries.ITEM.get(itemId);
                    if (!validateItemServer(item, itemId.toString(), ctx.getSource())) return 0;

                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), "1", 1, ctx.getSource().getRegistryManager());
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                })
                .then(CommandManager.argument("amount", StringArgumentType.string())
                    .executes(ctx -> { // /calc craft item <item_id_str> <amount>
                        Identifier itemId = Identifier.of(StringArgumentType.getString(ctx, "item_id_str"));
                        Item item = Registries.ITEM.get(itemId);
                        if (!validateItemServer(item, itemId.toString(), ctx.getSource())) return 0;
                        String amountStr = StringArgumentType.getString(ctx, "amount");
                        
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), amountStr, 1, ctx.getSource().getRegistryManager());
                        CalcCommand.sendMessageServer(ctx.getSource(), message);
                        return 1;
                    }))));
        
        craftLiteral.then(CommandManager.literal("item_depth")
            .then(CommandManager.argument("item_id_str", StringArgumentType.string())
                .suggests(new RecipeSuggestionProvider())
                .then(CommandManager.argument("depth_level", IntegerArgumentType.integer())
                    .executes(ctx -> { // /calc craft item_depth <item_id_str> <depth_level>
                        Identifier itemId = Identifier.of(StringArgumentType.getString(ctx, "item_id_str"));
                        Item item = Registries.ITEM.get(itemId);
                        if (!validateItemServer(item, itemId.toString(), ctx.getSource())) return 0;
                        int depth = IntegerArgumentType.getInteger(ctx, "depth_level");
                        
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), "1", depth, ctx.getSource().getRegistryManager());
                        CalcCommand.sendMessageServer(ctx.getSource(), message);
                        return 1;
                    })
                    .then(CommandManager.argument("amount_with_depth", StringArgumentType.string())
                        .executes(ctx -> { // /calc craft item_depth <item_id_str> <depth_level> <amount_with_depth>
                            Identifier itemId = Identifier.of(StringArgumentType.getString(ctx, "item_id_str"));
                            Item item = Registries.ITEM.get(itemId);
                            if (!validateItemServer(item, itemId.toString(), ctx.getSource())) return 0;
                            int depth = IntegerArgumentType.getInteger(ctx, "depth_level");
                            String amountStr = StringArgumentType.getString(ctx, "amount_with_depth");

                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), new ItemStack(item), amountStr, depth, ctx.getSource().getRegistryManager());
                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                            return 1;
                        })))));
        return craftLiteral; 
    }

    private static boolean ensureNamespace(String itemName, FabricClientCommandSource source) {
        if (!itemName.contains(":")) {
            source.sendError(Text.literal("Invalid item format: '"+itemName+"'. Please use a namespaced ID like 'minecraft:"+itemName+"'."));
            return false;
        }
        return true;
    }

    private static boolean validateItem(Item item, String itemName, FabricClientCommandSource source) {
        if (item == Items.AIR && !Objects.equals(itemName, "minecraft:air")) {
            source.sendError(Text.literal("Invalid item: " + itemName));
            return false;
        }
        return true;
    }

    private static boolean validateItemServer(Item item, String itemName, ServerCommandSource source) {
        if (item == Items.AIR && !Objects.equals(itemName, "minecraft:air")) {
            source.sendError(Text.literal("Invalid item: " + itemName));
            return false;
        }
        return true;
    }


    public static CalcMessageBuilder execute(Entity player, ItemStack targetItemStack, String amount, int steps, DynamicRegistryManager registryManager) {
        CalcMod.LOGGER.info("Executing craft with ItemStack: " + targetItemStack.getName().getString() + ", Amount: " + amount + ", Steps: " + steps);
        
        if (targetItemStack.getItem() == Items.AIR && !targetItemStack.isEmpty()) { 
            return new CalcMessageBuilder().addString("Invalid item: minecraft:air. Cannot craft air.");
        }
         if (targetItemStack.isEmpty()){ 
             return new CalcMessageBuilder().addString("Invalid item provided.");
         }


        RecipeManager recipeManager = player.getWorld().getRecipeManager();
        Recipe<?> foundRecipe = null;

        for (RecipeEntry<?> recipeEntry : recipeManager.values()) {
            Recipe<?> currentRecipe = recipeEntry.value();
            if (currentRecipe.getResult(registryManager) != null && currentRecipe.getResult(registryManager).getItem() == targetItemStack.getItem()) {
                RecipeSerializer<?> serializer = currentRecipe.getSerializer();
                if (serializer == RecipeSerializer.SHAPED || serializer == RecipeSerializer.SHAPELESS) {
                     foundRecipe = currentRecipe;
                     break; 
                }
            }
        }


        if (foundRecipe == null) {
            return new CalcMessageBuilder().addString("No crafting recipe found for " + targetItemStack.getName().getString());
        }

        DefaultedList<Ingredient> ingredientsList = foundRecipe.getIngredients();
        if (ingredientsList.isEmpty()) { 
             return new CalcMessageBuilder().addString(targetItemStack.getName().getString() + " is a base item or its recipe has no ingredients.");
        }

        int outputSize = foundRecipe.getResult(registryManager).getCount();
        if (outputSize == 0) { 
            return new CalcMessageBuilder().addString("Recipe for " + targetItemStack.getName().getString() + " has an output of 0, cannot calculate.");
        }
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        if (inputAmount <= 0) {
            return new CalcMessageBuilder().addString("Amount to craft must be positive.");
        }
        int baseCrafts = (int) Math.ceil(inputAmount / outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(recipeManager, registryManager, ingredientsList, baseCrafts, steps);
        
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), targetItemStack.getName().getString()}, new String[] {});
        
        for (Map.Entry<String, Map.Entry<ItemStack, Integer>> entry : ingredients.entrySet()) {
            String key = entry.getKey();
            ItemStack value = entry.getValue().getKey();
            int stackSize = value.getMaxCount();
            int totalItems = entry.getValue().getValue();
            double sb = Math.floor(totalItems / (double)(stackSize*27));
            String sbString = nf.format(sb);
            int remainder = totalItems % (stackSize*27);
            double stacks = Math.floor(remainder / (double)stackSize);
            String stacksString = nf.format(stacks);
            remainder = remainder % stackSize;
            String itemsString = nf.format(remainder);
            if (sb > 0) {
                messageBuilder.addString(key+": ");
                messageBuilder.addResult("SBs: "+sbString + ", Stacks: "+stacksString+", Items: "+itemsString+"\n");
            } else if (stacks > 0) {
                messageBuilder.addString(key + ": " );
                messageBuilder.addResult("Stacks: "+stacksString+", Items: "+itemsString+"\n");
            } else {
                messageBuilder.addString(key + ": " );
                messageBuilder.addResult("Items: "+itemsString+"\n");
            }
        }
        return messageBuilder;
    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(RecipeManager manager, DynamicRegistryManager registryManager, DefaultedList<Ingredient> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
        for (Ingredient ingredient : is) {
            if (ingredient.isEmpty()) continue; 
            ItemStack representativeStack = null;
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                if (stack.getItem() != Items.AIR) {
                    representativeStack = stack;
                    break;
                }
            }
            if (representativeStack == null && ingredient.getMatchingStacks().length > 0) {
                 representativeStack = ingredient.getMatchingStacks()[0]; 
            }

            if (representativeStack != null && representativeStack.getItem() != Items.AIR) {
                String itemName = representativeStack.getName().getString();
                ingredients.merge(itemName, Map.entry(representativeStack, amount_needed), (oldEntry, newEntry) -> Map.entry(oldEntry.getKey(), oldEntry.getValue() + newEntry.getValue()));
            }
        }
        
        if (steps <= 1) { 
            return ingredients;
        }

        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
        for (Map.Entry<String, Map.Entry<ItemStack, Integer>> currentIngredientEntry : ingredients.entrySet()) {
            ItemStack itemStack = currentIngredientEntry.getValue().getKey();
            int numNeeded = currentIngredientEntry.getValue().getValue();
            
            Optional<RecipeEntry<?>> recipeEntryOptional = manager.values().stream()
                .filter(re -> {
                    Recipe<?> recipe = re.value();
                    if (recipe.getResult(registryManager) == null || recipe.getResult(registryManager).getItem() != itemStack.getItem()) {
                        return false;
                    }
                    RecipeSerializer<?> serializer = recipe.getSerializer();
                    return serializer == RecipeSerializer.SHAPED || serializer == RecipeSerializer.SHAPELESS;
                })
                .findFirst();

            if (recipeEntryOptional.isPresent()) {
                Recipe<?> recipe = recipeEntryOptional.get().value();
                DefaultedList<Ingredient> subIngredientsList = recipe.getIngredients();
                if (subIngredientsList.isEmpty()) { 
                    ex_ingredients.merge(currentIngredientEntry.getKey(), currentIngredientEntry.getValue(), (oldVal, newVal) -> Map.entry(newVal.getKey(), oldVal.getValue() + newVal.getValue()));
                    continue;
                }
                int recipeOutputCount = recipe.getResult(registryManager).getCount();
                if (recipeOutputCount == 0) recipeOutputCount = 1; 

                int craftsNeededForSubItem = (int) Math.ceil((double) numNeeded / recipeOutputCount);
                HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients_map = getIngredients(manager, registryManager, subIngredientsList, craftsNeededForSubItem, steps - 1);
                
                for (Map.Entry<String, Map.Entry<ItemStack, Integer>> subEntry : sub_ingredients_map.entrySet()) {
                    ex_ingredients.merge(subEntry.getKey(), subEntry.getValue(), (oldVal, newVal) -> Map.entry(newVal.getKey(), oldVal.getValue() + newVal.getValue()));
                }
            } else { 
                 ex_ingredients.merge(currentIngredientEntry.getKey(), currentIngredientEntry.getValue(), (oldVal, newVal) -> Map.entry(newVal.getKey(), oldVal.getValue() + newVal.getValue()));
            }
        }
        return ex_ingredients;
    }

    public static String helpMessage = """
            §b§LCraft:§r§f
            Calculates the ingredients needed for crafting items.
            Base command §e/calc craft§r or §e/calc craft help§r shows this message.
            
            §eUsage: /calc craft item <item_id> [amount]§f
              Calculates ingredients for <item_id> with a crafting depth of 1.
              <item_id>: The namespaced ID of the item (e.g., minecraft:chest).
              [amount]: Optional. Number of items to craft (default: 1).
              Example: /calc craft item minecraft:furnace 3
              
            §eUsage: /calc craft item_depth <item_id> <depth_level> [amount]§f
              Calculates ingredients, breaking them down by <depth_level>.
              <depth_level>: How many sub-crafting steps to expand.
              [amount]: Optional. Number of items to craft (default: 1).
              Example: /calc craft item_depth minecraft:piston 2 5
            """;
    
}
