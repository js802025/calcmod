package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import java.util.Arrays;
import java.util.Collection;

import net.jsa2025.calcmod.CalcMod;
import net.minecraft.recipe.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CIdentifierArgumentType implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(id -> Text.stringifiedTranslatable("advancement.advancementNotFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(id -> Text.stringifiedTranslatable("recipe.notFound", id));

    public static CIdentifierArgumentType identifier() {
        return new CIdentifierArgumentType();
    }

    public static String getIdentifier(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, Identifier.class).getNamespace()+":"+context.getArgument(name, Identifier.class).getPath();
    }

    @Override
    public Identifier parse(final StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public static RecipeDisplayEntry getRecipeArgument(final CommandContext<FabricClientCommandSource> context, final String argumentName) throws CommandSyntaxException {
        String identifier = getIdentifier(context, argumentName);
        return context.getSource().getPlayer().getRecipeBook().getOrderedResults().stream().filter(x ->
                x.getAllRecipes().stream().anyMatch(i -> {
                         //   CalcMod.LOGGER.info(i.display().result().getStacks(SlotDisplayContexts.createParameters(context.getSource().getPlayer().getWorld())).get(0).getRegistryEntry().getIdAsString());
                    return i.display().result().getStacks(SlotDisplayContexts.createParameters(context.getSource().getPlayer().getEntityWorld())).get(0).getRegistryEntry().getIdAsString().equals(identifier);
                }
                )
                ).findFirst().get().getAllRecipes().stream().filter(i -> i.display().result().getStacks(SlotDisplayContexts.createParameters(context.getSource().getPlayer().getEntityWorld())).get(0).getRegistryEntry().getIdAsString().equals(identifier)).findFirst().get();

    }

    public static Recipe getRecipeArgumentServer(final CommandContext<ServerCommandSource> context, final String argumentName) throws CommandSyntaxException {
        Identifier identifier = context.getArgument(argumentName, Identifier.class);
//        return recipeManager.getPropertySet(identifier).
        CalcMod.LOGGER.info(identifier.toString());
        return context.getSource().getServer().getRecipeManager().values().stream().filter(val -> val.id().getValue().equals(identifier)).findFirst().get().value();

    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
