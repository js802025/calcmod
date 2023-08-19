package net.jsa2025.calcmod.commands.arguments.clientarguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class CLookingPosArgument implements CPosArgument {
    public static final char CARET = '^';
    private final double x;
    private final double y;
    private final double z;

    public CLookingPosArgument(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d toAbsolutePos(FabricClientCommandSource source) {
        Vec2f rotation = source.getRotation();
        Vec3d pos = CEntityAnchorArgumentType.EntityAnchor.FEET.positionAt(source);
        final float PiDividedBy180 = 0.017453292F;
        float f = MathHelper.cos((rotation.y + 90.0F) * PiDividedBy180);
        float g = MathHelper.sin((rotation.y + 90.0F) * PiDividedBy180);
        float h = MathHelper.cos(-rotation.x * PiDividedBy180);
        float i = MathHelper.sin(-rotation.x * PiDividedBy180);
        float j = MathHelper.cos((-rotation.x + 90.0F) * PiDividedBy180);
        float k = MathHelper.sin((-rotation.x + 90.0F) * PiDividedBy180);
        Vec3d vec3d2 = new Vec3d((f * h), i, (g * h));
        Vec3d vec3d3 = new Vec3d((f * j), k, (g * j));
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0D);
        double d = vec3d2.x * this.z + vec3d3.x * this.y + vec3d4.x * this.x;
        double e = vec3d2.y * this.z + vec3d3.y * this.y + vec3d4.y * this.x;
        double l = vec3d2.z * this.z + vec3d3.z * this.y + vec3d4.z * this.x;
        return new Vec3d(pos.x + d, pos.y + e, pos.z + l);
    }

    public Vec2f toAbsoluteRotation(FabricClientCommandSource source) {
        return Vec2f.ZERO;
    }

    public boolean isXRelative() {
        return true;
    }

    public boolean isYRelative() {
        return true;
    }

    public boolean isZRelative() {
        return true;
    }

    public static CLookingPosArgument parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        double x = readCoordinate(reader, cursor);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double y = readCoordinate(reader, cursor);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                double z = readCoordinate(reader, cursor);
                return new CLookingPosArgument(x, y, z);
            } else {
                reader.setCursor(cursor);
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
            }
        } else {
            reader.setCursor(cursor);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
        }
    }

    private static double readCoordinate(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw CoordinateArgument.MISSING_COORDINATE.createWithContext(reader);
        } else if (reader.peek() != '^') {
            reader.setCursor(startingCursorPos);
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(reader);
        }
        reader.skip();
        return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CLookingPosArgument lookingPosArgument)) {
            return false;
        } else {
            return this.x == lookingPosArgument.x && this.y == lookingPosArgument.y && this.z == lookingPosArgument.z;
        }
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }
}

