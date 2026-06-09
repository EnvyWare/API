package com.envyful.api.config.type.item;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.item.click.ClickAction;
import com.envyful.api.config.type.item.click.action.ExecuteConsoleCommandsClickAction;
import com.envyful.api.config.type.item.display.ItemDisplayRule;
import com.envyful.api.config.type.item.display.rule.RequiresPermissionDisplayRule;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.type.Pair;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class ConfigMenuItem {

    private ConfigItem item = new ConfigItem();
    private List<MenuPosition> positions = new ArrayList<>();
    private List<ItemDisplayRule> displayRules = new ArrayList<>();
    private List<ClickAction> actions = new ArrayList<>();

    public ConfigMenuItem() {}

    public ConfigMenuItem(ConfigItem item) {
        this.item = item;
    }

    public ResolvedMenuItem resolve(MenuItemContext context) {
        if (this.item == null || !this.item.isEnabled() || this.positions == null || this.positions.isEmpty()) {
            return ResolvedMenuItem.empty();
        }

        var resolvedItem = new ResolvedMenuItem(this.item, this.positions, this.actions);

        for (var displayRule : this.displayRules) {
            var result = displayRule.resolve(context, resolvedItem);

            if (result == null || result.isEmpty()) {
                return ResolvedMenuItem.empty();
            }

            resolvedItem = result;
        }

        return resolvedItem;
    }

    public void place(Pane pane, MenuItemContext context) {
        this.place(pane, context, null);
    }

    public void place(Pane pane, MenuItemContext context, ClickAction extraAction) {
        var resolvedItem = this.resolve(context);

        if (resolvedItem.isEmpty()) {
            return;
        }

        if (extraAction != null) {
            resolvedItem = resolvedItem.withAction(extraAction);
        }

        var displayable = this.createDisplayable(context, resolvedItem);

        for (var position : resolvedItem.positions()) {
            pane.set(position, displayable);
        }
    }

    private Displayable createDisplayable(MenuItemContext context, ResolvedMenuItem resolvedItem) {
        var builder = GuiFactory.convertConfigItemBuilder(resolvedItem.item(), context);

        for (var action : resolvedItem.actions()) {
            action.apply(builder);
        }

        var clickHandler = builder.clickHandler();

        builder.clickHandler((envyPlayer, clickType) -> {
            if (clickHandler != null) {
                clickHandler.accept(envyPlayer, clickType);
            }

            for (var action : resolvedItem.actions()) {
                action.handle(context, clickType);
            }
        });

        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ConfigMenuItem built = new ConfigMenuItem();

        protected Builder() {
        }

        public Builder enable(boolean enabled) {
            this.built.item.setEnabled(enabled);
            return this;
        }

        public Builder enable() {
            return this.enable(true);
        }

        public Builder disable() {
            return this.enable(false);
        }

        public Builder type(String type) {
            this.built.item.setType(type);
            return this;
        }

        public Builder amount(int amount) {
            return this.amount(amount + "");
        }

        public Builder amount(String amount) {
            this.built.item.setAmount(amount);
            return this;
        }

        public Builder name(String name) {
            this.built.item.setName(name);
            return this;
        }

        public Builder flags(String... flags) {
            this.built.item.getFlags().addAll(List.of(flags));
            return this;
        }

        public Builder lore(String... lore) {
            this.built.item.getLore().addAll(List.of(lore));
            return this;
        }

        public Builder enchants(ConfigItem.EnchantData... enchantData) {
            Map<String, ConfigItem.EnchantData> enchantMap = this.built.item.getEnchants();

            for (var enchant : enchantData) {
                enchantMap.put("" + enchantMap.size(), enchant);
            }

            this.built.item.setEnchants(enchantMap);
            return this;
        }

        public Builder nbt(String key, ConfigItem.NBTValue value) {
            this.built.item.getNbt().put(key, value);
            return this;
        }

        public Builder nbt(String key, int value) {
            return this.nbt(key, new ConfigItem.NBTValue("int", String.valueOf(value)));
        }

        public Builder nbt(String key, double value) {
            return this.nbt(key, new ConfigItem.NBTValue("double", String.valueOf(value)));
        }

        public Builder nbt(String key, long value) {
            return this.nbt(key, new ConfigItem.NBTValue("long", String.valueOf(value)));
        }

        public Builder nbt(String key, String value) {
            return this.nbt(key, new ConfigItem.NBTValue("string", value));
        }

        public Builder nbt(String key, short value) {
            return this.nbt(key, new ConfigItem.NBTValue("short", String.valueOf(value)));
        }

        public Builder nbt(String key, float value) {
            return this.nbt(key, new ConfigItem.NBTValue("float", String.valueOf(value)));
        }

        public Builder nbt(String key, byte value) {
            return this.nbt(key, new ConfigItem.NBTValue("byte", String.valueOf(value)));
        }

        public Builder nbt(Map<String, ConfigItem.NBTValue> nbt) {
            this.built.item.getNbt().putAll(nbt);
            return this;
        }

        @Deprecated
        @SafeVarargs
        public final Builder positions(Pair<Integer, Integer>... positions) {
            for (var position : positions) {
                this.built.positions.add(MenuPosition.fromPair(position));
            }

            return this;
        }

        public Builder positions(int x, int y) {
            this.built.positions.add(new MenuPosition(x, y));
            return this;
        }

        public Builder clearPositions() {
            this.built.positions.clear();
            return this;
        }

        public Builder setPositions(Map<String, Pair<Integer, Integer>> positions) {
            this.built.positions.clear();
            for (var entry : positions.entrySet()) {
                this.built.positions.add(MenuPosition.fromPair(entry.getValue()));
            }
            return this;
        }

        public Builder setEnchants(Map<String, ConfigItem.EnchantData> enchants) {
            this.built.item.getEnchants().clear();
            this.built.item.getEnchants().putAll(enchants);
            return this;
        }

        public Builder clearEnchants() {
            this.built.item.getEnchants().clear();
            return this;
        }

        public Builder noPermission() {
            return this;
        }

        public Builder requiresPermission(
                String permission,
                ConfigItem elseItem) {
            this.built.displayRules.add(new RequiresPermissionDisplayRule(permission, elseItem));
            return this;
        }

        public Builder remainOpenOnClick() {
            return this;
        }

        public Builder closeOnClick() {
            this.built.actions.add(ClickAction.close());
            return this;
        }

        public Builder executeCommands(String... commands) {
            this.built.actions.add(new ExecuteConsoleCommandsClickAction(commands));
            return this;
        }

        public Builder dataComponents(CommentedConfigurationNode components) {
            this.built.item.setComponents(components);
            return this;
        }

        public ConfigMenuItem build() {
            return this.built;
        }
    }
}
