package net.jsa2025.calcmod.commands.subcommands;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.Dynamic;

import dev.xpple.clientarguments.arguments.CIdentifierArgumentType;
import net.jsa2025.calcmod.commands.arguments.CRecipeSuggestionProvider;
import net.jsa2025.calcmod.commands.arguments.RecipeSuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.jsa2025.calcmod.commands.CalcCommand;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.jsa2025.calcmod.utils.CalcMessageBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.registry.DynamicRegistryManager;


public class Craft {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> register(LiteralArgumentBuilder<FabricClientCommandSource> command, CommandRegistryAccess registry) {
        command
        .then(ClientCommandManager.literal("craft").then(ClientCommandManager.argument("item", CIdentifierArgumentType.identifier()).suggests(new CRecipeSuggestionProvider())
        .then(ClientCommandManager.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), CIdentifierArgumentType.getCRecipeArgument(ctx, "item"), StringArgumentType.getString(ctx, "amount"), ctx.getSource().getRegistryManager());
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })))
        .then(ClientCommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessage(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerServer(LiteralArgumentBuilder<ServerCommandSource> command, CommandRegistryAccess registry) {
        command
        .then(CommandManager.literal("craft").then(CommandManager.argument("item", IdentifierArgumentType.identifier()).suggests(new RecipeSuggestionProvider())
        .then(CommandManager.argument("amount", StringArgumentType.greedyString())
        .executes((ctx) -> {
            CalcMessageBuilder message = execute(ctx.getSource().getPlayer(), IdentifierArgumentType.getRecipeArgument(ctx, "item"), StringArgumentType.getString(ctx, "amount"), ctx.getSource().getRegistryManager());
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })))
        .then(CommandManager.literal("help").executes(ctx -> {
            CalcMessageBuilder message = Help.execute("craft");
            CalcCommand.sendMessageServer(ctx.getSource(), message);
            return 1;
        })));
        return command;
    }


    public static CalcMessageBuilder execute(PlayerEntity player, Recipe item, String amount, DynamicRegistryManager registryManager) {

        var is = item.getIngredients();
        var outputSize = item.getOutput(registryManager).getCount();
        double inputAmount = Math.floor(CalcCommand.getParsedExpression(player.getBlockPos(), amount));
        int a = (int) Math.ceil(inputAmount/outputSize);
        Map<String, Integer> ingredients = new HashMap<String, Integer>();
        Map<String, ItemStack> ingredientsStacks = new HashMap<String, ItemStack>();
        for (Object i : is) {
            Ingredient ingredient = (Ingredient) i;
            if (ingredient.getMatchingStacks().length > 0) {
                if (ingredients.containsKey(ingredient.getMatchingStacks()[0].getName().getString())) {
                    

                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), ingredients.get(ingredient.getMatchingStacks()[0].getName().getString()) + a );
                } else {
                    ingredients.put(ingredient.getMatchingStacks()[0].getName().getString(), a);
                    ingredientsStacks.put(ingredient.getMatchingStacks()[0].getName().getString(), ingredient.getMatchingStacks()[0]);
                }
                
            //ingredients.merge(ingredient.getMatchingStacks()[0], a, Integer::sum);
            }
        }
        CalcMessageBuilder messageBuilder = new CalcMessageBuilder()
                .addFromArray(new String[] {"Ingredients needed for crafting ", "input", " ", "input", "(s): \n"}, new String[] {nf.format(inputAmount), item.getOutput(registryManager).getName().getString()}, new String[] {});
        
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            int stackSize = ingredientsStacks.get(entry.getKey()).getMaxCount();
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
        §b§LCraft:§r§f
        Given a desired item and the quantity to be crafted §7§o(can be in expression form)§r§f, returns the amounts of the items needed to craft the amount of the desired item.
            §eUsage: /calc craft <item> <amount>§f
            """;
    
}
