package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;



import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraftforge.registries.ForgeRegistries;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
//        command
//        .then(Commands.literal("craft").then(Commands.argument("item", ResourceLocationArgument.id()).suggests(new RecipeSuggestionProvider())
//        .then(Commands.argument("amount", StringArgumentType.greedyString())
//        .executes((ctx) -> {
////            String item = StringArgumentType.getString(ctx, "item");
////==            Optional<? extends Recipe<?>> itemR = ctx.getSource().getRecipeManager().byKey(ResourceLocation.tryParse(item));
//            Recipe<?> itemR = ResourceLocationArgument.getRecipe(ctx, "item").value();
//            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), itemR, StringArgumentType.getString(ctx, "amount"), ctx.getSource().registryAccess());
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })))
//        .then(Commands.literal("help").executes(ctx -> {
//            CalcMessageBuilder message = Help.execute("craft");
//            CalcCommand.sendMessageServer(ctx.getSource(), message);
//            return 0;
//        })));
//        return command;
        command
                .then(Commands.literal("craft").then(Commands.argument("item", ResourceKeyArgument.key(Registries.RECIPE))
                                .then(Commands.literal("depth").then( Commands.argument("level", IntegerArgumentType.integer())
                                        .then(Commands.argument("amount", StringArgumentType.greedyString())
                                                .executes((ctx) -> {

                                                    CalcMessageBuilder message = execute(ctx.getSource().getEntity(), ResourceKeyArgument.getRecipe(ctx, "item"), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().registryAccess());
                                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                                    return 1;
                                                })))
                                ).then(Commands.argument("amount", StringArgumentType.greedyString())
                                        .executes((ctx) -> {
                                            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), ResourceKeyArgument.getRecipe(ctx, "item"), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().registryAccess());
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
        var item = (recipeHolder.value().display().get(0));
        ContextMap map = SlotDisplayContext.fromLevel(player.level());
        List<ItemStack> is;
        if (item.type().equals(ShapedCraftingRecipeDisplay.TYPE)) {
            is = ((ShapedCraftingRecipeDisplay) item).ingredients().stream().map(i-> i.resolveForFirstStack(map)).toList();
        } else {
            is = ((ShapelessCraftingRecipeDisplay) item).ingredients().stream().map(i-> i.resolveForFirstStack(map)).toList();

        }
        var outputSize = item.result().resolveForFirstStack(map).getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);
//        Map<String, Integer> ingredients = new HashMap<String, Integer>();
//        Map<String, ItemStack> ingredientsStacks = new HashMap<String, ItemStack>();
//        for (Object i : is) {
//            Ingredient ingredient = (Ingredient) i;
//            if (ingredient.getItems().length > 0) {
//                if (ingredients.containsKey(ingredient.getDisplayName().getString())) {
//
//
//                    ingredients.put(ingredient.getDisplayName().getString(), ingredients.get(ingredient.getDisplayName().getString()) + a );
//                } else {
//                    ingredients.put(ingredient.getDisplayName().getString(), a);
//                    ingredientsStacks.put(ingredient.getDisplayName().getString(), ingredient);
//                }
//
//                //ingredients.merge(ingredient.getMatchingStacks()[0], a, Integer::sum);
//            }
//        }
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player.level().getServer().getRecipeManager(), map, is, a, steps);

        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), processItemName(item.result().resolveForFirstStack(map).getDisplayName().getString())}, new String[] {});

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

        //     message.set(0, "Ingredients needed for crafting "+nf.format(inputAmount)+" "+item.getOutput(registryManager).getName().getString()+"s: \n"+message.get(0));

        return messageBuilder;
    }

    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(RecipeManager manager, ContextMap map, List<ItemStack> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
        for (ItemStack ingredient : is) {

            //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());

            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
            if (ingredient.getCount() > 0) {
                if (ingredients.containsKey(processItemName(ingredient.getDisplayName().getString()))) {
                    ingredients.put(processItemName(ingredient.getDisplayName().getString()), Map.entry(ingredients.get(processItemName(ingredient.getDisplayName().getString())).getKey(), ingredients.get(processItemName(ingredient.getDisplayName().getString())).getValue()+amount_needed));
                } else {
                    //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
                    ingredients.put(processItemName(ingredient.getDisplayName().getString()), Map.entry(ingredient, amount_needed));
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
                Optional<ResourceKey<?>> ing_id = Optional.ofNullable(ForgeRegistries.ITEMS.getResourceKey(ingredient.getKey().getItem()).get());
                //  CalcMod.LOGGER.info(ing_id.get().getPath());
                //    if (ing_id.get().getPath() .contains("ingot")) {

                Optional<ResourceKey<?>> finalIng_id = ing_id;
                //  CalcMod.LOGGER.info(finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath() .split("_")[0] + "_block");

                try {
                    Optional<RecipeDisplay> recipe = manager.getRecipes().stream().filter(i ->
                            !i.value().display().isEmpty()).map(x ->
                            x.value().display().size() > 1 ? x.value().display().stream().filter(i -> Objects.equals(x.id().registry().getPath(), finalIng_id.get().registry().getPath() + "_from_" + finalIng_id.get().registry().getPath().split("_")[0] + "_block")).findFirst().orElse(x.value().display().get(0)) : !x.value().display().isEmpty() ? x.value().display().get(0) : null).filter(i -> Objects.equals(finalIng_id.get(), ForgeRegistries.ITEMS.getResourceKey(i.result().resolveForFirstStack(map).getItem()).get()) && (i.type().equals(ShapedCraftingRecipeDisplay.TYPE) || i.type().equals(ShapelessCraftingRecipeDisplay.TYPE))).findFirst();

//                    ing_id = Optional.of(manager.getRecipes().stream().map(x ->
//                            Objects.equals(x.id().registry().getPath(), finalIng_id.get().getPath() + "_from_" + finalIng_id.get().getPath().split("_")[0] + "_block")
//                    ).findFirst().get().id().registryKey());
                    // }


                    if (recipe.isPresent()) {
                        // Recipe<?> recipe = manager.byKey(ing_id.get()).get().value();
                        List<ItemStack> sis;
                        if (recipe.get().type().equals(ShapedCraftingRecipeDisplay.TYPE)) {
                            sis = ((ShapedCraftingRecipeDisplay) recipe.get()).ingredients().stream().map(i -> i.resolveForFirstStack(map)).toList();
                        } else {
                            sis = ((ShapelessCraftingRecipeDisplay) recipe.get()).ingredients().stream().map(i -> i.resolveForFirstStack(map)).toList();

                        }

                        //   CalcMod.LOGGER.info(String.valueOf(ingredient.get()));
                        //    CalcMod.LOGGER.info(String.valueOf(recipe.getResult(registryManager).getCount()));
                        //      CalcMod.LOGGER.info(String.valueOf((double) ingredient.getValue() / (double) recipe.getResult(registryManager).getCount()));
                        HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(manager, map, sis, (int) Math.ceil((double) ingredient.getValue() / (double) recipe.get().result().resolveForFirstStack(map).getCount()), steps - 1);
                        //     CalcMod.LOGGER.info(recipe.getResult(registryManager).getName().getString());
                        //     ingredients.remove(recipe.getResult(registryManager).getName().getString());
                        for (String item : sub_ingredients.keySet()) {
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
        }

        return ex_ingredients;
    }

    private static String processItemName(String name) {
        if (name.length() > 1) {
            return name.substring(1, name.length()-1);
        }
        return name;
    }

    public static String helpMessage = """
        §b§LCraft:§r§f
        Given an item and the quanity you want to craft of it, returns the amounts of the ingredients needed to craft the quantity of the item.
        §eUsage: /calc craft <item> <amount>§f
            """;
    
}
