package com.envyful.api.config.type.item;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.item.click.ClickAction;

import java.util.ArrayList;
import java.util.List;

public class ResolvedMenuItem {

    private static final ResolvedMenuItem EMPTY = new ResolvedMenuItem(null, List.of(), List.of());

    private final ConfigItem item;
    private final List<MenuPosition> positions;
    private final List<ClickAction> actions;

    public ResolvedMenuItem(ConfigItem item, List<MenuPosition> positions, List<ClickAction> actions) {
        this.item = item;
        this.positions = positions;
        this.actions = actions;
    }

    public static ResolvedMenuItem empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return this.item == null;
    }

    public ConfigItem item() {
        return this.item;
    }

    public List<MenuPosition> positions() {
        return this.positions;
    }

    public List<ClickAction> actions() {
        return this.actions;
    }

    public ResolvedMenuItem withAction(ClickAction action) {
        var newActions = new ArrayList<>(this.actions);
        newActions.add(action);
        return new ResolvedMenuItem(this.item, this.positions, newActions);
    }

    public ResolvedMenuItem withoutActions() {
        return new ResolvedMenuItem(this.item, this.positions, List.of());
    }

    public ResolvedMenuItem fallback(ConfigItem fallback) {
        return new ResolvedMenuItem(fallback, this.positions, List.of());
    }
}
