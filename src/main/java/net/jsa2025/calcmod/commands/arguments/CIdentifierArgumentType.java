package net.jsa2025.calcmod.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import java.util.Arrays;
import java.util.Collection;

import net.jsa2025.calcmod.CalcMod;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CIdentifierArgumentType implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(id -> Component.translatable("advancement.advancementNotFound", id));
    private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(id -> Component.translatable("recipe.notFound", id));

    public static CIdentifierArgumentType identifier() {
        return new CIdentifierArgumentType();
    }

    public static String getIdentifier(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, Identifier.class).getNamespace()+":"+context.getArgument(name, Identifier.class).getPath();
    }

    @Override
    public Identifier parse(final StringReader stringReader) throws CommandSyntaxException {
        return Identifier.parse(stringReader.getString());
    }

    public static RecipeDisplayEntry getRecipeArgument(final CommandContext<FabricClientCommandSource> context, final String argumentName) throws CommandSyntaxException {
        String identifier = getIdentifier(context, argumentName);
        return context.getSource().getPlayer().getRecipeBook().getCollections().stream().filter(x ->
                x.getRecipes().stream().anyMatch(i -> {
                         //   CalcMod.LOGGER.info(i.display().result().getStacks(SlotDisplayContexts.createParameters(context.getSource().getPlayer().getWorld())).get(0).getRegistryEntry().getIdAsString());
                    return BuiltInRegistries.ITEM.getKey(i.display().result().resolveForStacks(SlotDisplayContext.fromLevel(context.getSource().getLevel())).get(0).getItem()).toString().equals(identifier);
                }
                )
                ).findFirst().get().getRecipes().stream().filter(i -> BuiltInRegistries.ITEM.getKey(i.display().result().resolveForStacks(SlotDisplayContext.fromLevel(context.getSource().getLevel())).get(0).getItem()).toString().equals(identifier)).findFirst().get();

    }



    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
