package net.jsa2025.calcmod.commands.subcommands;




import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.CalcMod;
import net.jsa2025.calcmod.commands.CalcCommand;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import net.jsa2025.calcmod.commands.arguments.CIdentifierArgumentType;
import net.jsa2025.calcmod.commands.arguments.CRecipeSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;





public class ReverseCraft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command
                .then(ClientCommandManager.literal("craftinv").then(ClientCommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new CRecipeSuggestionProvider()).executes((ctx) -> {
                    CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgument(ctx, "item"));
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }))
                .then(ClientCommandManager.literal("help").executes(ctx -> {
                    CalcMessageBuilder message = Help.execute("craftinv");
                    CalcCommand.sendMessage(ctx.getSource(), message);
                    return 1;
                }))
                );
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command) {
        command
                .then(CommandManager.literal("reverseCraft").then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new RecipeSuggestionProvider()).executes((ctx) -> {
                    CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getRecipeArgumentServer(ctx, "item"));
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }))
                .then(CommandManager.literal("help").executes(ctx -> {
                    CalcMessageBuilder message = Help.execute("craftinv");
                    CalcCommand.sendMessageServer(ctx.getSource(), message);
                    return 1;
                }))
                );
        return command;
    }

    @Environment(EnvType.CLIENT)
    public static CalcMessageBuilder execute(PlayerEntity player, RecipeDisplayEntry item) {
        HashMap<Ingredient, Integer> itemsHaved = new HashMap<>();
        HashMap<Ingredient, Integer> ingCount = new HashMap<>();

        int outputSize = item.display().result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getCount();
        item.craftingRequirements().get().stream().forEach(ing -> {
            ingCount.put(ing, ingCount.getOrDefault(ing, 0) + 1);
        });
        ingCount.keySet().forEach(ing -> {
            itemsHaved.put(ing, 0);
            player.getInventory().spliterator().forEachRemaining(stack -> {
            if (stack.getName().getString().contains("Shulker Box")) {
                ((ContainerComponent) stack.getComponents().stream().filter(c -> c.value().getClass().equals(ContainerComponent.class)).findFirst().get().value()).iterateNonEmpty().forEach(shulkerStack -> {
                        if (ing.acceptsItem(shulkerStack.getRegistryEntry())) {
                            if (itemsHaved.containsKey(ing)) {
                                itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.getCount());
                            } else {
                                itemsHaved.put(ing, shulkerStack.getCount());
                            }
                        }
                });
            } else if (stack.getName().getString().contains("Bundle")) {
                ((BundleContentsComponent) stack.getComponents().stream().filter(c -> c.value().getClass().equals(BundleContentsComponent.class)).findFirst().get().value()).stream().forEach(shulkerStack -> {
                    if (ing.acceptsItem(shulkerStack.getRegistryEntry())) {
                        if (itemsHaved.containsKey(ing)) {
                            itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.getCount());
                        } else {
                            itemsHaved.put(ing, shulkerStack.getCount());
                        }
                    }
                });
            }

                CalcMod.LOGGER.info(ing.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString());
                if (ing.acceptsItem(stack.getRegistryEntry())) {
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
        };
      //  itemsHaved.keySet().stream().forEach(i -> CalcMod.LOGGER.info(i.toDisplay().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()));
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " craftable with inventory items: ", "result"}, new String[] {item.display().result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()},new String[] {String.valueOf(canCraft)});
        return message;
    }
    public static CalcMessageBuilder execute(PlayerEntity player, Recipe item) {
        HashMap<Ingredient, Integer> itemsHaved = new HashMap<>();
        HashMap<Ingredient, Integer> ingCount = new HashMap<>();

        int outputSize = ((RecipeDisplay)item.getDisplays().get(0)).result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getCount();
        item.getIngredientPlacement().getIngredients().forEach(ing -> {
            ingCount.put(ing, ingCount.getOrDefault(ing, 0) + 1);
        });
        ingCount.keySet().forEach(ing -> {
            itemsHaved.put(ing, 0);
            player.getInventory().spliterator().forEachRemaining(stack -> {
                if (stack.getName().getString().contains("Shulker Box")) {
                    ((ContainerComponent) stack.getComponents().stream().filter(c -> c.value().getClass().equals(ContainerComponent.class)).findFirst().get().value()).iterateNonEmpty().forEach(shulkerStack -> {
                        if (ing.acceptsItem(shulkerStack.getRegistryEntry())) {
                            if (itemsHaved.containsKey(ing)) {
                                itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.getCount());
                            } else {
                                itemsHaved.put(ing, shulkerStack.getCount());
                            }
                        }
                    });
                } else if (stack.getName().getString().contains("Bundle")) {
                    ((BundleContentsComponent) stack.getComponents().stream().filter(c -> c.value().getClass().equals(BundleContentsComponent.class)).findFirst().get().value()).stream().forEach(shulkerStack -> {
                        if (ing.acceptsItem(shulkerStack.getRegistryEntry())) {
                            if (itemsHaved.containsKey(ing)) {
                                itemsHaved.put(ing, itemsHaved.get(ing) + shulkerStack.getCount());
                            } else {
                                itemsHaved.put(ing, shulkerStack.getCount());
                            }
                        }
                    });
                }
                if (ing.acceptsItem(stack.getRegistryEntry())) {
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
        };
        CalcMessageBuilder message = new CalcMessageBuilder().addFromArray(new String[] {"input", " craftable with inventory items: ", "result"}, new String[] {((RecipeDisplay)item.getDisplays().get(0)).result().getFirst(SlotDisplayContexts.createParameters(player.getEntityWorld())).getName().getString()},new String[] {String.valueOf(canCraft)});
        return message;
    }

    public static String helpMessage = """
            §b§LCraft With Inventory:§r§f
                   Given an item, returns the maximum number of that item the player can craft using their current inventory\s
                        §eUsage: /calc craftinv <item>§f
            """;
}
