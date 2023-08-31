package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;

import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import net.minecraft.command.Commands;import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import net.jsa2025.calcmod.utils.CalcMessageBuilder;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    

    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("craft").then(Commands.argument("item", ResourceLocationArgument.id()).suggests(new RecipeSuggestionProvider())
        .then(Commands.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
//            String item = StringArgumentType.getString(ctx, "item");
//==            Optional<? extends Recipe<?>> itemR = ctx.getSource().getRecipeManager().byKey(ResourceLocation.tryParse(item));
            Recipe<?> itemR = ResourceLocationArgument.getRecipe(ctx, "item");
            CalcMessageBuilder message = execute(ctx.getSource().getEntity(), itemR, StringArgumentType.getString(ctx, "amount"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })));
        return command;
    }


    public static CalcMessageBuilder execute(Entity player, Recipe<?> item, String amount) {

        var is = item.getIngredients();
        var outputSize = item.getResultItem().getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player, amount));
        int a = (int) Math.ceil(inputAmount/outputSize);
        Map<String, Integer> ingredients = new HashMap<String, Integer>();
        Map<String, ItemStack> ingredientsStacks = new HashMap<String, ItemStack>();
        for (Object i : is) {
            Ingredient ingredient = (Ingredient) i;
            if (ingredient.getItems().length > 0) {
                if (ingredients.containsKey(ingredient.getItems()[0].getDisplayName().getString())) {
                    

                    ingredients.put(ingredient.getItems()[0].getDisplayName().getString(), ingredients.get(ingredient.getItems()[0].getDisplayName().getString()) + a );
                } else {
                    ingredients.put(ingredient.getItems()[0].getDisplayName().getString(), a);
                    ingredientsStacks.put(ingredient.getItems()[0].getDisplayName().getString(), ingredient.getItems()[0]);
                }

                //ingredients.merge(ingredient.getMatchingStacks()[0], a, Integer::sum);
            }
        }
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients to craft ", "input", " ", "input", ": \n"}, new String[] {nf.format(inputAmount), item.getResultItem().getDisplayName().getString()}, new String[] {});

        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            int stackSize = ingredientsStacks.get(entry.getKey()).getMaxStackSize();
            double sb = Math.floor(value/(stackSize*27));
            String sbString = nf.format(sb);
            int remainder = value % (stackSize*27);
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

    public static String helpMessage = """
        §LCraft:§r
        Given an item and the quanity you want to craft of it, returns the amounts of the ingredients needed to craft the quantity of the item.
        §cUsage: /calc craft <item> <amount>§f
            """;
    
}
