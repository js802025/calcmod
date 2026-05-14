package net.jsa2025.calcmod.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReverseCraft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
                .then(Commands.literal("craftinv").then(Commands.argument("item", ResourceKeyArgument.key(Registries.RECIPE)).executes((ctx) -> {
                                    CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), ResourceKeyArgument.getRecipe(ctx, "item"));
                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                    return 1;
                                }))
                                .then(Commands.literal("help").executes(ctx -> {
                                    CalcMessageBuilder message = Help.execute("craftinv");
                                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                                    return 1;
                                }))
                );
        return command;
    }

    public static CalcMessageBuilder execute(Player player, RecipeHolder<?> recipeHolder) {
        var item = (recipeHolder.value().display().get(0));
        HashMap<Ingredient, Integer> itemsHaved = new HashMap<>();
        HashMap<Ingredient, Integer> ingCount = new HashMap<>();
        ContextMap map = SlotDisplayContext.fromLevel(player.level());

        var outputSize = item.result().resolveForFirstStack(map).getCount();
        recipeHolder.value().placementInfo().ingredients().forEach(ing -> {
            ingCount.put(ing, ingCount.getOrDefault(ing, 0) + 1);
        });
        ingCount.keySet().forEach(ing -> {
            itemsHaved.put(ing, 0);
            player.getInventory().spliterator().forEachRemaining(stack -> {
                if (stack.getItemName().getString().contains("Shulker Box")) {
                    ((ItemContainerContents) stack.getComponents().stream().filter(c -> c.value().getClass().equals(ItemContainerContents.class)).findFirst().get().value()).nonEmptyItems().forEach(shulkerStack -> {
                        if (ing.acceptsItem(shulkerStack.item())) {
                            if (itemsHaved.containsKey(ing)) {
                                itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.count());
                            } else {
                                itemsHaved.put(ing, shulkerStack.count());
                            }
                        }
                    });
                } else if (stack.getItemName().getString().contains("Bundle")) {
                    ((BundleContents) stack.getComponents().stream().filter(c -> c.value().getClass().equals(BundleContents.class)).findFirst().get().value()).items().forEach(shulkerStack -> {
                        if (ing.acceptsItem(shulkerStack.item())) {
                            if (itemsHaved.containsKey(ing)) {
                                itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.count());
                            } else {
                                itemsHaved.put(ing, shulkerStack.count());
                            }
                        }
                    });
                }
                if (ing.acceptsItem(stack.typeHolder())) {
                    CalcMod.LOGGER.info("YES");
                    if (itemsHaved.containsKey(ing)) {
                        itemsHaved.put(ing, itemsHaved.get(ing) + stack.getCount());
                    } else {
                        itemsHaved.put(ing, stack.getCount());
                    }
                }
            });
        });
        int canCraft = Integer.MAX_VALUE;
        for (var ing : itemsHaved.keySet()) {
            int amount = itemsHaved.get(ing);
            if (amount / ingCount.get(ing) < canCraft) {
                canCraft = (amount / ingCount.get(ing)) * outputSize;
            }
        }
        ;
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder().addFromArray(new String[]{"input", " craftable with inventory items: "}, new String[]{item.result().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getItemName().getString()}, new String[]{});
        double stackSize = item.result().resolveForFirstStack(SlotDisplayContext.fromLevel(player.level())).getMaxStackSize();
        double sb = Math.floor(canCraft / (stackSize * 27));
        String sbString = nf.format(sb);
        double remainder = canCraft % (stackSize * 27);
        double stacks = Math.floor(remainder / stackSize);
        String stacksString = nf.format(stacks);
        remainder = remainder % stackSize;
        String items = nf.format(remainder);
        if (sb > 0) {
            messageBuilder.addResult("SBs: " + sbString + ", Stacks: " + stacksString + ", Items: " + items);
        } else if (stacks > 0) {
            messageBuilder.addResult("Stacks: " + stacksString + ", Items: " + items);
        } else {
            messageBuilder.addResult("Items: " + items);
        }
        return messageBuilder;
    }
    public static String helpMessage = """
            §b§LCraft With Inventory:§r§f
                   Given an item, returns the maximum number of that item the player can craft using their current inventory\s
                        §eUsage: /calc craftinv <item>§f
            """;
}
