package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.CIdentifierArgumentType;
import net.jsa2025.calcmod.commands.arguments.CRecipeSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.text.NumberFormat;
import java.util.*;

@Environment(EnvType.CLIENT)
public class CraftClient {
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command, CommandBuildContext registry) {
        command
                .then(ClientCommands.literal("craft").then(ClientCommands.argument("item", IdentifierArgument.id()).suggests(new CRecipeSuggestionProvider())
                                .then(ClientCommands.literal("depth").then( ClientCommands.argument("level", IntegerArgumentType.integer())
                                        .then(ClientCommands.argument("amount", StringArgumentType.greedyString())
                                                .executes((ctx) -> {
                                                    CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), ctx.getSource().getPlayer().getRecipeBook(), CIdentifierArgumentType.getRecipeArgument(ctx, "item"), StringArgumentType.getString(ctx, "amount"), IntegerArgumentType.getInteger(ctx, "level"), ctx.getSource().registryAccess());
                                                    CalcCommand.sendMessage(ctx.getSource(), message);
                                                    return 1;
                                                })))
                                ).then(ClientCommands.argument("amount", StringArgumentType.greedyString())
                                        .executes((ctx) -> {
                                            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), ctx.getSource().getPlayer().getRecipeBook(), CIdentifierArgumentType.getRecipeArgument(ctx, "item"), StringArgumentType.getString(ctx, "amount"), 1, ctx.getSource().registryAccess());
                                            CalcCommand.sendMessage(ctx.getSource(), message);
                                            return 1;
                                        })))
                        .then(ClientCommands.literal("help").executes(ctx -> {
                            CalcMessageBuilder message = Help.execute("craft");
                            CalcCommand.sendMessage(ctx.getSource(), message);
                            return 1;
                        })));
        return command;
    }
    public static CalcMessageBuilder execute(Player player, ClientRecipeBook book, RecipeDisplayEntry item, String amount, int steps, RegistryAccess registryManager) {
        var is = item.craftingRequirements();
        var outputSize = item.display().result().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);

        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = getIngredients(player, book, registryManager, is, a, steps);
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), item.display().result().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString()}, new String[] {});

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
    static HashMap<String, Map.Entry<ItemStack, Integer>> getIngredients(Player player, ClientRecipeBook book, RegistryAccess registryManager, Optional<List<Ingredient>> is, int amount_needed, int steps) {
        HashMap<String, Map.Entry<ItemStack, Integer>> ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();
        //    CalcMod.LOGGER.info("Step"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
        for (Ingredient ingredient : is.get()) {
            //    ingredient.getMatchingItems().get(0).getKey().get().toString()

            //     CalcMod.LOGGER.info("Step1"+steps+is.get(0).getMatchingStacks()[0].getName().getString());

            //        CalcMod.LOGGER.info(manager.get(ingredient.getMatchingStacks()[0].getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
            if (!ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).isEmpty()) {
                if (ingredients.containsKey(ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString())) {
                    ingredients.put(ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString(), Map.entry(ingredients.get(ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString()).getKey(), ingredients.get(ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString()).getValue()+amount_needed));
                } else {
                    //         CalcMod.LOGGER.info("Step2"+steps+is.get(0).getMatchingStacks()[0].getName().getString());
                    ingredients.put(ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString(), Map.entry(ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())), amount_needed));
                }
                //      CalcMod.LOGGER.info("Step4"+ingredients.get(ingredient.getMatchingItems().get(0).getIdAsString()).getValue());

            }
        }
        HashMap<String, Map.Entry<ItemStack, Integer>> ex_ingredients = new HashMap<String, Map.Entry<ItemStack, Integer>>();

        for (Map.Entry<ItemStack, Integer> ingredient : ingredients.values()) {
            //   CalcMod.LOGGER.info("ING "+steps+": "+ingredient.getKey().getName().getString());
            if (steps == 1) {
                // CalcMod.LOGGER.info(is.get(0).getMatchingStacks()[0].getName().getString());
                return  ingredients;
            } else {
                // CalcMod.LOGGER.info("new");
                //     CalcMod.LOGGER.info(manager.get(ingredient.getRegistryEntry().getKey().get().getValue()).get().value().getIngredients().get(0).getMatchingStacks()[0].getName().getString());
                Identifier ing_id = BuiltInRegistries.ITEM.getKey(ingredient.getKey().getItem());


                Optional<RecipeCollection> recipeResultCollection = book.getCollections().stream().filter(x ->
                        x.getRecipes().stream().anyMatch(i -> {
                            String id = BuiltInRegistries.ITEM.getKey(i.display().result().resolveForStacks(SlotDisplayContext.fromLevel(player.level())).get(0).getItem()).toString();
                            return id.equals(ing_id.toString());
                        })).findFirst();
                if (recipeResultCollection.isPresent()) {


                    RecipeDisplayEntry recipeDisplayEntry = recipeResultCollection.get().getRecipes().stream().filter(i -> {
                        String id = BuiltInRegistries.ITEM.getKey(i.display().result().resolveForStacks(SlotDisplayContext.fromLevel(player.level())).get(0).getItem()).toString();
                        return id.equals(ing_id.toString());                    }).findFirst().get();
                    Optional<List<Ingredient>> sis = recipeDisplayEntry.craftingRequirements();
                    HashMap<String, Map.Entry<ItemStack, Integer>> sub_ingredients = getIngredients(player, book, registryManager, sis, (int) Math.ceil((double) ingredient.getValue() / (double) recipeDisplayEntry.display().result().resolveForStacks(SlotDisplayContext.fromLevel(player.level())).get(0).getCount()), steps - 1);
                    for (String item : sub_ingredients.keySet()) {
                        //   CalcMod.LOGGER.info("Sub ing: "+item);
                        if (ex_ingredients.containsKey(item)) {
                            ex_ingredients.put(item, Map.entry(ingredients.get(item).getKey(), ingredients.get(item).getValue() + sub_ingredients.get(item).getValue()));
                        } else {
                            ex_ingredients.put(item, Map.entry(sub_ingredients.get(item).getKey(), sub_ingredients.get(item).getValue()));
                        }
                    }
                } else {
                    //  CalcMod.LOGGER.info("NO MATCH FOUND for "+ing_id.get().toString());

                    ex_ingredients.put(ingredient.getKey().getItemName().getString(), Map.entry(ingredient.getKey(), ingredient.getValue()));
                }
                //   return ingredients;
            }
        }

        return ex_ingredients;
    }
}
