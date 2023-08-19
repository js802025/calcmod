package net.jsa2025.calcmod.commands.arguments.clientarguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class CEntityAnchorArgumentType implements ArgumentType<CEntityAnchorArgumentType.EntityAnchor> {

    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(name -> new TranslatableText("argument.anchor.invalid", name));

    public static CEntityAnchorArgumentType entityAnchor() {
        return new CEntityAnchorArgumentType();
    }

    public static EntityAnchor getCEntityAnchor(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, EntityAnchor.class);
    }

    @Override
    public EntityAnchor parse(final StringReader stringReader) throws CommandSyntaxException {
        int cursor = stringReader.getCursor();
        String string = stringReader.readUnquotedString();
        EntityAnchor entityAnchor = EntityAnchor.fromId(string);
        if (entityAnchor == null) {
            stringReader.setCursor(cursor);
            throw INVALID_ANCHOR_EXCEPTION.createWithContext(stringReader, string);
        }
        return entityAnchor;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EntityAnchor.ANCHORS.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public enum EntityAnchor {
        FEET("feet", (pos, entity) -> pos),
        EYES("eyes", (pos, entity) -> new Vec3d(pos.x, pos.y + (double) entity.getStandingEyeHeight(), pos.z));

        static final Map<String, EntityAnchor> ANCHORS = Util.make(Maps.newHashMap(), map -> {
            EntityAnchor[] var1 = values();

            for (EntityAnchor entityAnchor : var1) {
                map.put(entityAnchor.id, entityAnchor);
            }
        });
        private final String id;
        private final BiFunction<Vec3d, Entity, Vec3d> offset;

        EntityAnchor(String id, BiFunction<Vec3d, Entity, Vec3d> offset) {
            this.id = id;
            this.offset = offset;
        }

        @Nullable
        public static CEntityAnchorArgumentType.EntityAnchor fromId(String id) {
            return ANCHORS.get(id);
        }

        public Vec3d positionAt(Entity entity) {
            return this.offset.apply(entity.getPos(), entity);
        }

        public Vec3d positionAt(FabricClientCommandSource source) {
            Entity entity = source.getEntity();
            return entity == null ? source.getPosition() : this.offset.apply(source.getPosition(), entity);
        }
    }
}
