package com.envyful.api.config.type.item;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.parse.ComposedPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class MenuItemContext extends ComposedPlaceholder {

    private final EnvyPlayer<?> player;

    public MenuItemContext(EnvyPlayer<?> player, Placeholder... placeholders) {
        this(player, List.of(placeholders));
    }

    public MenuItemContext(EnvyPlayer<?> player, List<Placeholder> placeholders) {
        super(compilePlaceholders(player, placeholders));

        this.player = player;
    }

    private static List<Placeholder> compilePlaceholders(EnvyPlayer<?> player, List<Placeholder> placeholders) {
        var list = new ArrayList<Placeholder>(placeholders.size() + 1);
        list.addAll(placeholders);
        list.add(player);
        return list;
    }

    public EnvyPlayer<?> player() {
        return this.player;
    }

    public List<Placeholder> placeholders() {
        return this.placeholders;
    }

    public MenuItemContext withPlaceholders(Placeholder... placeholders) {
        var newPlaceholders = new ArrayList<>(this.placeholders);
        newPlaceholders.remove(this.player);
        newPlaceholders.addAll(List.of(placeholders));

        return new MenuItemContext(this.player, newPlaceholders);
    }
}
