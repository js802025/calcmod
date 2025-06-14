package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CIdentifierArgumentType implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    //private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(id -> Text.stringifiedTranslatable("advancement.advancementNotFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(id -> Text.stringifiedTranslatable("recipe.notFound", id));

    public static CIdentifierArgumentType identifier() {
        return new CIdentifierArgumentType();
    }

    public static Identifier getIdentifier(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, Identifier.class);
    }

    @Override
    public Identifier parse(final StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public static RecipeEntry<?> getRecipeArgument(final CommandContext<FabricClientCommandSource> context, final String argumentName) throws CommandSyntaxException {
        RecipeManager recipeManager = context.getSource().getWorld().getRecipeManager();
        Identifier identifier = getIdentifier(context, argumentName);
        return recipeManager.get(identifier).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create(identifier));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
