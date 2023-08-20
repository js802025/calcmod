package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;


import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;

import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.commands.Commands;import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
    

    public static LiteralArgumentBuilder<CommandSourceStack> registerServer(LiteralArgumentBuilder<CommandSourceStack> command) {
        command
        .then(Commands.literal("craft").then(Commands.argument("item", StringArgumentType.string()).suggests(new RecipeSuggestionProvider())
        .then(Commands.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            String item = StringArgumentType.getString(ctx, "item");
            Optional<? extends Recipe<?>> itemR = ctx.getSource().getRecipeManager().byKey(ResourceLocation.tryParse(item));
            String[] message = execute(ctx.getSource().getPlayerOrException(), itemR.get(), StringArgumentType.getString(ctx, "amount"));
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


    public static String[] execute(ServerPlayer player, Recipe item, String amount) {

        var is = item.getIngredients();
        var outputSize = item.getResultItem().getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player.getOnPos(), amount));
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
        message.set(0, "Ingredients needed for crafting "+nf.format(inputAmount)+" "+item.getResultItem().getDisplayName().getString()+"s: \n"+message.get(0));

        
        return message.toArray(new String[message.size()]);
    }

    public static String helpMessage = """
        §LCraft:§r
        Given an item and the quanity you want to craft of it, returns the amounts of the ingredients needed to craft the quantity of the item.
        §cUsage: /calc craft <item> <amount>§f
            """;
    
}
