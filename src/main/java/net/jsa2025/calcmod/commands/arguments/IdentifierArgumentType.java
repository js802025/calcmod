package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.jsa2025.calcmod.CalcMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;

public class IdentifierArgumentType {
    public static Recipe getRecipeArgument(final CommandContext<CommandSourceStack> context, final String argumentName) throws CommandSyntaxException {
        Identifier identifier = IdentifierArgument.getId(context, argumentName);
//        return recipeManager.getPropertySet(identifier).
        CalcMod.LOGGER.info(identifier.toString());
        return context.getSource().getServer().getRecipeManager().getRecipes().stream().filter(val -> val.id().identifier().equals(identifier)).findFirst().get().value();

    }
}
