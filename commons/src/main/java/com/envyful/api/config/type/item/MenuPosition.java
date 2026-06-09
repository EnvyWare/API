package com.envyful.api.config.type.item;

import com.envyful.api.type.Pair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

@ConfigSerializable
public class MenuPosition {

    private int x;
    private int y;

    public MenuPosition() {
    }

    public MenuPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return this.x;
    }

    public int getX() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int getY() {
        return this.y;
    }

    public MenuPosition setX(int x) {
        this.x = x;
        return this;
    }

    public MenuPosition setY(int y) {
        this.y = y;
        return this;
    }

    public MenuPosition set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public MenuPosition add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public MenuPosition subtract(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public MenuPosition multiply(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public MenuPosition divide(int x, int y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public int toSlot(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }

        return this.y * width + this.x;
    }

    public boolean isWithin(int width, int height) {
        return this.x >= 0 && this.x < width && this.y >= 0 && this.y < height;
    }

    public Pair<Integer, Integer> toPair() {
        return Pair.of(this.x, this.y);
    }

    public MenuPosition copy() {
        return new MenuPosition(this.x, this.y);
    }

    public static MenuPosition of(int x, int y) {
        return new MenuPosition(x, y);
    }

    public static MenuPosition fromSlot(int slot, int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }

        int x = slot % width;
        int y = slot / width;
        return new MenuPosition(x, y);
    }

    public static MenuPosition fromPair(Pair<Integer, Integer> pair) {
        return new MenuPosition(pair.getX(), pair.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MenuPosition)) {
            return false;
        }

        var that = (MenuPosition) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "MenuPosition{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
