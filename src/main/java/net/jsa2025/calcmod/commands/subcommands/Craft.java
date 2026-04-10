package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;

public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
            .then(Commands.literal("craft").then(Commands.argument("item", ResourceLocationArgument.id()).suggests(new RecipeSuggestionProvider())
                .then(Commands.literal("depth").then(Commands.argument("level", IntegerArgumentType.integer())
                    .then(Commands.argument("amount", StringArgumentType.greedyString())
                        .executes((ctx) -> {
                            ResourceLocation recipeId = ResourceLocationArgument.getId(ctx, "item");
                            Optional<? extends RecipeHolder<?>> holder = ctx.getSource().getRecipeManager().byKey(recipeId);
                            if (holder.isEmpty()) {
                                CalcCommand.sendMessageServer(ctx.getSource(), new CalcMessageBuilder("§cRecipe not found.§f"));
                                return 0;
                            }
                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), holder.get(), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().registryAccess());
                            CalcCommand.sendMessageServer(ctx.getSource(), message);
                            return 1;
                        })))
                ).then(Commands.argument("amount", StringArgumentType.greedyString())
                    .executes((ctx) -> {
                        ResourceLocation recipeId = ResourceLocationArgument.getId(ctx, "item");
                        Optional<? extends RecipeHolder<?>> holder = ctx.getSource().getRecipeManager().byKey(recipeId);
                        if (holder.isEmpty()) {
                            CalcCommand.sendMessageServer(ctx.getSource(), new CalcMessageBuilder("§cRecipe not found.§f"));
                            return 0;
                        }
                        CalcMessageBuilder message = execute(ctx.getSource().getEntity(), holder.get(), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().registryAccess());
                        CalcCommand.sendMessageServer(ctx.getSource(), message);
                        return 1;
                    })))
                .then(Commands.literal("help").executes(ctx -> {
                    CalcMessageBuilder message = Help.execute("craft");
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                })));
        return command;
    }

    public static CalcMessageBuilder execute(Entity player, RecipeHolder<?> recipeHolder, String amount, int steps, RegistryAccess registryAccess) {
        Recipe<?> recipe = recipeHolder.value();
        ItemStack resultStack = recipe.getResultItem(registryAccess);
        var outputSize = resultStack.getCount();

        List<Ingredient> ingredients;
        if (recipe instanceof ShapedRecipe shaped) {
            ingredients = shaped.getIngredients();
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            ingredients = shapeless.getIngredients();
        } else {
            return new CalcMessageBuilder("§cUnsupported recipe type.§f");
        }

        // Resolve ingredients to ItemStacks
        List<ItemStack> ingredientStacks = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            ItemStack[] items = ingredient.getItems();
            if (items.length > 0) {
                ingredientStacks.add(items[0].copy());
            }
        }

        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount / outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredientMap = getIngredients(player.getCommandSenderWorld().getServer().getRecipeManager(), registryAccess, ingredientStacks, a, steps);

        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
            .addFromArray(new String[]{"Ingredients to craft ", "input", " ", "input", ": \n"},
                new String[]{nf.format(inputAmount), processItemName(resultStack.getDisplayName().getString())},
                new String[]{});

        for (Map.Entry<String, Map.Entry<ItemStack, Integer>> entry : ingredientMap.entrySet()) {
            String key = entry.getKey();
            ItemStack value = entry.getValue().getKey();
            int stackSize = value.getMaxStackSize();
            double sb = Math.floor((double) entry.getValue().getValue() / (stackSize * 27));
            String sbString = nf.format(sb);
            int remainder = entry.getValue().getValue() % (stackSize * 27);
            double stacks = Math.floor((double) remainder / stackSize);
            String stacksString = nf.format(stacks);
            remainder = remainder % stackSize;
            String items = nf.format(remainder);
            if (sb > 0) {
                messageBuilder.addString(key + ": ");
                messageBuilder.addResult("SBs: " + sbString + ", Stacks: " + stacksString + ", Items: " + items + "\n");
            } else if (stacks > 0) {
                messageBuilder.addString(key + ": ");
                messageBuilder.addResult("Stacks: " + stacksString + ", Items: " + items + "\n");
            } else {
                messageBuilder.addString(key + ": ");
                messageBuilder.addResult("Items: " + items + "\n");
            }
        }

        return messageBuilder;
    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(RecipeManager manager, RegistryAccess registryAccess, List<ItemStack> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<>();
        CalcMod.LOGGER.info("Step");
        for (ItemStack ingredient : is) {
            if (ingredient.getCount() > 0) {
                String name = processItemName(ingredient.getDisplayName().getString());
                if (ingredients.containsKey(name)) {
                    ingredients.put(name, Map.entry(ingredients.get(name).getKey(), ingredients.get(name).getValue() + amount_needed));
                } else {
                    ingredients.put(name, Map.entry(ingredient, amount_needed));
                }
            }
        }

        if (steps <= 1) {
            return ingredients;
        }

        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<>();
        CalcMod.LOGGER.info("Step1");

        for (Map.Entry<ItemStack, Integer> ingredient : ingredients.values()) {
            try {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(ingredient.getKey().getItem());
                CalcMod.LOGGER.info("Step1.5");

                // Find a crafting recipe that produces this item
                Optional<RecipeHolder<CraftingRecipe>> recipe = manager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                    .filter(r -> {
                        ItemStack result = r.value().getResultItem(registryAccess);
                        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
                        return resultId.equals(itemId);
                    })
                    .findFirst();

                if (recipe.isPresent()) {
                    Recipe<?> subRecipe = recipe.get().value();
                    List<ItemStack> subIngredientStacks = new ArrayList<>();

                    List<Ingredient> subIngredients;
                    if (subRecipe instanceof ShapedRecipe shaped) {
                        subIngredients = shaped.getIngredients();
                    } else if (subRecipe instanceof ShapelessRecipe shapeless) {
                        subIngredients = shapeless.getIngredients();
                    } else {
                        ex_ingredients.put(processItemName(ingredient.getKey().getDisplayName().getString()), Map.entry(ingredient.getKey(), ingredient.getValue()));
                        continue;
                    }

                    for (Ingredient ing : subIngredients) {
                        ItemStack[] items = ing.getItems();
                        if (items.length > 0) {
                            subIngredientStacks.add(items[0].copy());
                        }
                    }

                    CalcMod.LOGGER.info("Step3");
                    ItemStack subResult = subRecipe.getResultItem(registryAccess);
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(manager, registryAccess, subIngredientStacks, (int) Math.ceil((double) ingredient.getValue() / (double) subResult.getCount()), steps - 1);

                    for (String item : sub_ingredients.keySet()) {
                        CalcMod.LOGGER.info(item);
                        if (ex_ingredients.containsKey(item)) {
                            ex_ingredients.put(item, Map.entry(ex_ingredients.get(item).getKey(), ex_ingredients.get(item).getValue() + sub_ingredients.get(item).getValue()));
                        } else {
                            ex_ingredients.put(item, Map.entry(sub_ingredients.get(item).getKey(), sub_ingredients.get(item).getValue()));
                        }
                    }
                } else {
                    ex_ingredients.put(processItemName(ingredient.getKey().getDisplayName().getString()), Map.entry(ingredient.getKey(), ingredient.getValue()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ex_ingredients;
    }

    private static String processItemName(String name) {
        if (name.length() > 1) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }

    public static String helpMessage = """
        §b§LCraft:§r§f
        Given an item and the quanity you want to craft of it, returns the amounts of the ingredients needed to craft the quantity of the item.
        §eUsage: /calc craft <item> <amount>§f
            """;
}
