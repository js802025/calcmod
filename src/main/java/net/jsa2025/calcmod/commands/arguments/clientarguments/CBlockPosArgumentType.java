package net.jsa2025.calcmod.commands.arguments.clientarguments;

//ported from clientarguments by xpple

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandSource.RelativePosition;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CBlockPosArgumentType implements ArgumentType<CPosArgument> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.outofworld"));
    public static final SimpleCommandExceptionType OUT_OF_BOUNDS_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.outofbounds"));

    public static CBlockPosArgumentType blockPos() {
        return new CBlockPosArgumentType();
    }

    public static BlockPos getCLoadedBlockPos(final CommandContext<FabricClientCommandSource> context, final String name) throws CommandSyntaxException {
        ClientWorld clientWorld = context.getSource().getWorld();
        return getCLoadedBlockPos(context, clientWorld, name);
    }

    public static BlockPos getCLoadedBlockPos(final CommandContext<FabricClientCommandSource> context, final ClientWorld world, final String name) throws CommandSyntaxException {
        BlockPos blockPos = getCBlockPos(context, name);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) {
            throw UNLOADED_EXCEPTION.create();
        } else if (!world.isInBuildLimit(blockPos)) {
            throw OUT_OF_WORLD_EXCEPTION.create();
        } else {
            return blockPos;
        }
    }

    public static BlockPos getCBlockPos(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, CPosArgument.class).toAbsoluteBlockPos(context.getSource());
    }

    public static BlockPos getCValidBlockPos(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
        BlockPos blockPos = getCBlockPos(context, name);
        if (!World.isValid(blockPos)) {
            throw OUT_OF_BOUNDS_EXCEPTION.create();
        }
        return blockPos;
    }

    @Override
    public CPosArgument parse(final StringReader stringReader) throws CommandSyntaxException {
        return stringReader.canRead() && stringReader.peek() == '^' ? CLookingPosArgument.parse(stringReader) : CDefaultPosArgument.parse(stringReader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof CommandSource)) {
            return Suggestions.empty();
        }
        String string = builder.getRemaining();
        Collection<CommandSource.RelativePosition> collection;
        if (!string.isEmpty() && string.charAt(0) == '^') {
            collection = Collections.singleton(RelativePosition.ZERO_LOCAL);
        } else {
            collection = ((CommandSource) context.getSource()).getBlockPositionSuggestions();
        }

        return CommandSource.suggestPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
