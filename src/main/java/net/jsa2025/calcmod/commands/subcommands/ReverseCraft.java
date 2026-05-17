package net.jsa2025.calcmod.commands.subcommands;




import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;


public class ReverseCraft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));


    public static LiteralArgumentBuilder<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
                .then(Commands.literal("craftinv").then(Commands.argument("item", ArgumentTypes.namespacedKey()).suggests(new RecipeSuggestionProvider()).executes((ctx) -> {
                    CalcMessageBuilder message = execute((Player) ctx.getSource().getExecutor(), ctx.getSource().getExecutor().getServer().getRecipe(ctx.getArgument("item", NamespacedKey.class)));
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }))
                .then(Commands.literal("help").executes(ctx -> {
                    CalcMessageBuilder message = Help.execute("craftinv");
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                })));
        return command;
    }

    public static CalcMessageBuilder execute(Player player, org.bukkit.inventory.Recipe item) {
        HashMap<RecipeChoice, Integer> itemsHaved = new HashMap<>();
        HashMap<RecipeChoice, Integer> ingCount = new HashMap<>();
        List<RecipeChoice> is;
        if (item.getClass().getName().contains("Shaped")) {
            is = ((ShapedRecipe) item).getChoiceMap().values().stream().filter(Objects::nonNull).toList();
        } else {
            is = ((ShapelessRecipe) item).getChoiceList().stream().filter(Objects::nonNull).toList();
        }

        var outputSize = item.getResult().getAmount();
        is.forEach(ing -> {
            ingCount.put(ing, ingCount.getOrDefault(ing, 0) + 1);
        });
        ingCount.keySet().forEach(ing -> {
            itemsHaved.put(ing, 0);
            player.getInventory().spliterator().forEachRemaining(stack -> {
                if (stack != null) {
                    CalcMod.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(stack.effectiveName()));
                    if (PlainTextComponentSerializer.plainText().serialize(stack.effectiveName()).contains("Shulker Box")) {
                        if (!(stack.getItemMeta() instanceof BlockStateMeta meta)) return;

                        if (!(meta.getBlockState() instanceof ShulkerBox shulker)) return;
                        CalcMod.LOGGER.info("Shulker: " + shulker.getInventory());
                        shulker.getInventory().forEach(shulkerStack -> {
                            if (shulkerStack != null) {
                                if (ing.test(shulkerStack)) {
                                    if (itemsHaved.containsKey(ing)) {
                                        itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.getAmount());
                                    } else {
                                        itemsHaved.put(ing, shulkerStack.getAmount());
                                    }
                                }
                            }
                        });
                    } else  if (PlainTextComponentSerializer.plainText().serialize(stack.effectiveName()).contains("Bundle")) {
                        if (!(stack.getItemMeta() instanceof BlockStateMeta meta)) return;

                        if (!(meta.getBlockState() instanceof BundleMeta bundle)) return;
                        bundle.getItems().forEach(shulkerStack -> {
                            if (shulkerStack != null) {
                                if (ing.test(shulkerStack)) {
                                    if (itemsHaved.containsKey(ing)) {
                                        itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.getAmount());
                                    } else {
                                        itemsHaved.put(ing, shulkerStack.getAmount());
                                    }
                                }
                            }
                    });
                    }
                    if (ing.test(stack)) {
                        if (itemsHaved.containsKey(ing)) {
                            itemsHaved.put(ing, itemsHaved.get(ing) + stack.getAmount());
                        } else {
                            itemsHaved.put(ing, stack.getAmount());
                        }
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
        };
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder().addFromArray(new String[] {"input", " craftable with inventory items: "}, new String[] {PlainTextComponentSerializer.plainText().serialize(item.getResult().effectiveName())},new String[] {});
        double stackSize = item.getResult().getMaxStackSize();
        double sb = Math.floor(canCraft/(stackSize*27));
        String sbString = nf.format(sb);
        double remainder = canCraft % (stackSize*27);
        double stacks = Math.floor(remainder/stackSize);
        String stacksString = nf.format(stacks);
        remainder = remainder % stackSize;
        String items = nf.format(remainder);
        if (sb > 0) {
            messageBuilder.addResult("SBs: "+sbString + ", Stacks: "+stacksString+", Items: "+items);
        } else if (stacks > 0) {
            messageBuilder.addResult("Stacks: "+stacksString+", Items: "+items);
        } else {
            messageBuilder.addResult("Items: "+items);
        }
        return messageBuilder;
    }

    public static String helpMessage = """
            <aqua><bold>Craft With Inventory:<reset><white>
                   Given an item, returns the maximum number of that item the player can craft using their current inventory\s
                        <yellow>Usage: /calc craftinv <item><white>
            """;
}
