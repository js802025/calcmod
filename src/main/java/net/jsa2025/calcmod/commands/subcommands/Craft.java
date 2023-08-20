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
import net.minecraft.entity.Entity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    

    public static LiteralArgumentBuilder<CommandSource> registerServer(LiteralArgumentBuilder<CommandSource> command) {
        command
        .then(Commands.literal("craft").then(Commands.argument("item", StringArgumentType.string()).suggests(new RecipeSuggestionProvider())
        .then(Commands.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String item = StringArgumentType.getString(ctx, "item");
            IRecipe itemR = ctx.getSource().getServer().getRecipeManager().getRecipe(ResourceLocation.makeResourceLocation(item)); // (ResourceLocation.tryParse(item));
            String[] message = execute(ctx.getSource().getEntity(), itemR, StringArgumentType.getString(ctx, "amount"));
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 0;
        })))
        .then(Commands.literal("help").executes(ctx -> {
            String[] message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message, true);
            return 0;
        })));
        return command;
    }


    public static String[] execute(Entity player, IRecipe item, String amount) {

        NonNullList<Ingredient> is = item.getIngredients();
        int outputSize = item.getRecipeOutput().getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player.getPosition(), amount));
        int a = (int) Math.ceil(inputAmount/outputSize);
        Map<String, Integer> ingredients = new HashMap<String, Integer>();
        Map<String, ItemStack> ingredientsStacks = new HashMap<String, ItemStack>();
        for (Object i : is) {
            Ingredient ingredient = (Ingredient) i;
            if (ingredient.getMatchingStacks().length > 0) {
                if (ingredients.containsKey(ingredient.getMatchingStacks()[0].getDisplayName().getString())) {
                    

                    ingredients.put(ingredient.getMatchingStacks()[0].getDisplayName().getString(), ingredients.get(ingredient.getMatchingStacks()[0].getDisplayName().getString()) + a );
                } else {
                    ingredients.put(ingredient.getMatchingStacks()[0].getDisplayName().getString(), a);
                    ingredientsStacks.put(ingredient.getMatchingStacks()[0].getDisplayName().getString(), ingredient.getMatchingStacks()[0]);
                }
                
            //ingredients.merge(ingredient.getMatchingStacks()[0], a, Integer::sum);
            }
        }
       List<String> message = new ArrayList<String>();
        
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
                message.add(key+": ");
                message.add("SBs: "+sbString + ", Stacks: "+stacksString+", Items: "+items+"\n");
            } else if (stacks > 0) {
                message.add(key + ": " );
                message.add("Stacks: "+stacksString+", Items: "+items+"\n");
            } else {
                message.add(key + ": " );
                message.add("Items: "+items+"\n");
            }
        }
        message.set(0, "Ingredients needed for crafting "+nf.format(inputAmount)+" "+item.getRecipeOutput().getDisplayName().getString()+"s: \n"+message.get(0));

        
        return message.toArray(new String[message.size()]);
    }

    public static String helpMessage = "§LCraft:§r \nGiven an item and the quanity you want to craft of it, returns the amounts of the ingredients needed to craft the quantity of the item. \n§cUsage: /calc craft <item> <amount>§f";
    
}
