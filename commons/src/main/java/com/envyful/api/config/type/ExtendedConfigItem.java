package com.envyful.api.config.type;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.Pair;
import com.envyful.api.type.UtilParse;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.*;

/**
 *
 * An extended version of the {@link ConfigItem} that allows for more advanced
 * configuration options such as requiring permission to view the item, executing commands
 * on click, and more.
 *
 */
@ConfigSerializable
public class ExtendedConfigItem {

    private boolean enabled = true;
    private String type = "minecraft:black_stained_glass_pane";
    private String amount = "1";
    private String name = " ";
    private List<String> flags = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    private Map<String, ConfigItem.EnchantData> enchants = new HashMap<>();
    private Map<String, ConfigItem.NBTValue> nbt = new HashMap<>();
    private Map<String, Pair<Integer, Integer>> positions = new HashMap<>();
    private boolean requiresPermission;
    private String permission;
    private ConfigItem elseItem;
    private boolean closeOnClick;
    private List<String> commandsExecuted;
    private CommentedConfigurationNode components = CommentedConfigurationNode.root();

    public ExtendedConfigItem() {
    }

    private ExtendedConfigItem(boolean enabled, String type,
                               String amount, String name, List<String> flags,
                               List<String> lore,
                               Map<String, ConfigItem.EnchantData> enchants,
                               Map<String, ConfigItem.NBTValue> nbt,
                               Map<String, Pair<Integer, Integer>> positions,
                               boolean requiresPermission,
                               String permission, ConfigItem elseItem,
                               boolean closeOnClick,
                               List<String> commandsExecuted, CommentedConfigurationNode components) {
        this.enabled = enabled;
        this.type = type;
        this.amount = String.valueOf(amount);
        this.name = name;
        this.flags = flags;
        this.lore = lore;
        this.enchants = enchants;
        this.nbt = nbt;
        this.positions = positions;
        this.requiresPermission = requiresPermission;
        this.permission = permission;
        this.elseItem = elseItem;
        this.closeOnClick = closeOnClick;
        this.commandsExecuted = commandsExecuted;
        this.components = components;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getType() {
        return this.type;
    }

    public int getAmount() {
        return UtilParse.parseInt(this.amount).orElse(0);
    }

    public int getAmount(List<Placeholder> placeholders) {
        List<Integer> integers = PlaceholderFactory.handlePlaceholders(
                Collections.singletonList(amount),
                s -> UtilParse.parseInt(s).orElse(0),
                placeholders);

        if (integers.isEmpty()) {
            return 0;
        }

        return integers.get(0);
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public Map<String, ConfigItem.EnchantData> getEnchants() {
        return enchants;
    }

    public List<String> getFlags() {
        return flags;
    }

    public Map<String, ConfigItem.NBTValue> getNbt() {
        return this.nbt;
    }

    public List<Pair<Integer, Integer>> getPositions() {
        return List.copyOf(this.positions.values());
    }

    public boolean requiresPermission() {
        return this.requiresPermission;
    }

    public String getPermission() {
        return this.permission;
    }

    public ConfigItem getElseItem() {
        return this.elseItem;
    }

    public boolean shouldCloseOnClick() {
        return this.closeOnClick;
    }

    public List<String> getCommandsExecuted() {
        return this.commandsExecuted;
    }

    public boolean hasPermission(EnvyPlayer<?> player) {
        if (!this.requiresPermission || this.permission == null || this.permission.isEmpty() ||
        this.permission.equalsIgnoreCase("none")) {
            return true;
        }

        return player.hasPermission(this.permission);
    }

    public ConfigItem asConfigItem() {
        return new InternalConfigItem(this);
    }

    public Displayable toDisplayable(Placeholder... placeholders) {
        return this.toDisplayableBuilder(placeholders).build();
    }

    public Displayable.Builder<?> toDisplayableBuilder(Placeholder... placeholders) {
        var builder = this.toDisplayableBuilder(placeholders);

        if (builder == null) {
            return null;
        }

        builder.clickHandler((clicker, clickType) -> {
            PlatformProxy.runSync(() -> {
                if (this.shouldCloseOnClick()) {
                    clicker.closeInventory();
                }

                for (var command : this.getCommandsExecuted()) {
                    clicker.executeCommand(command);
                }
            });
        });

        return builder;
    }

    public Displayable.Builder<?> convert(EnvyPlayer<?> player, Placeholder... placeholders) {
        if (!this.isEnabled()) {
            return null;
        }

        if (this.hasPermission(player)) {
            var builder = GuiFactory.convertConfigItemBuilder(this.asConfigItem(), placeholders);

            if (this.shouldCloseOnClick() || !this.getCommandsExecuted().isEmpty()) {
                builder.clickHandler((clicker, clickType) -> {
                    PlatformProxy.runSync(() -> {
                        if (this.shouldCloseOnClick()) {
                            clicker.closeInventory();
                        }

                        for (var command : this.getCommandsExecuted()) {
                            clicker.executeCommand(command);
                        }
                    });
                });
            }

            return builder;
        }

        if (this.getElseItem() == null || !this.getElseItem().isEnabled()) {
            return null;
        }

        return GuiFactory.convertConfigItemBuilder(this.getElseItem(), placeholders);
    }

    public Displayable.Builder<?> convertToBuilder(EnvyPlayer<?> player, Placeholder... placeholders) {
        return new ExtendedConfigItemDisplayableBuilder<>(this, player, placeholders);
    }

    public Displayable.Builder<?> convertToBuilder(EnvyPlayer<?> player, Pane pane, Placeholder... placeholders) {
        return new ExtendedConfigItemDisplayableBuilder<>(this, player, pane, placeholders);
    }

    public Displayable convert(EnvyPlayer<?> player, Pane pane, Placeholder... placeholders) {
        return new ExtendedConfigItemDisplayableBuilder<>(this, player, pane, placeholders).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copy(ExtendedConfigItem configItem) {
        var builder = builder()
                .enable(configItem.enabled)
                .type(configItem.type)
                .amount(configItem.amount)
                .name(configItem.name)
                .flags(configItem.flags.toArray(new String[0]))
                .lore(configItem.lore.toArray(new String[0]))
                .enchants(configItem.enchants.values().toArray(new ConfigItem.EnchantData[0]))
                .nbt(configItem.nbt)
                .positions(configItem.positions.values().toArray(new Pair[0]))
                .executeCommands(configItem.commandsExecuted.toArray(new String[0]))
                .dataComponents(configItem.components);

        if (configItem.requiresPermission) {
            builder.requiresPermission(configItem.permission, configItem.elseItem);
        }

        if (builder.closeOnClick) {
            builder.closeOnClick();
        } else {
            builder.remainOpenOnClick();
        }

        return builder;
    }

    public static class Builder {

        private boolean enabled = true;
        private String type = "minecraft:black_stained_glass_pane";
        private String amount = "1";
        private String name = " ";
        private boolean requiresPermission = false;
        private String permission;
        private ConfigItem elseItem;
        private boolean closeOnClick = false;
        private CommentedConfigurationNode components = CommentedConfigurationNode.root();

        private final Map<String, ConfigItem.EnchantData> enchants = new HashMap<>();
        private final List<String> commandsExecuted = new ArrayList<>();
        private final List<String> flags = new ArrayList<>();
        private final List<String> lore = new ArrayList<>();
        private final Map<String, ConfigItem.NBTValue> nbt = new HashMap<>();
        private final Map<String, Pair<Integer, Integer>> positions = new HashMap<>();


        protected Builder() {
        }

        public Builder enable(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder enable() {
            this.enabled = true;
            return this;
        }

        public Builder disable() {
            this.enabled = false;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(int amount) {
            return this.amount(amount + "");
        }

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder flags(String... flags) {
            this.flags.addAll(List.of(flags));
            return this;
        }

        public Builder lore(String... lore) {
            this.lore.addAll(List.of(lore));
            return this;
        }

        public Builder enchants(ConfigItem.EnchantData... enchantData) {
            for (ConfigItem.EnchantData enchantDatum : enchantData) {
                this.enchants.put(this.enchants.size() + "", enchantDatum);
            }

            return this;
        }

        public Builder nbt(String key, ConfigItem.NBTValue value) {
            this.nbt.put(key, value);
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
            this.nbt.putAll(nbt);
            return this;
        }

        @Deprecated
        @SafeVarargs
        public final Builder positions(Pair<Integer, Integer>... positions) {
            for (Pair<Integer, Integer> position : positions) {
                this.positions.put(this.positions.size() + "", position);
            }

            return this;
        }

        public Builder positions(int x, int y) {
            this.positions.put(this.positions.size() + "", Pair.of(x, y));
            return this;
        }

        public Builder clearPositions() {
            this.positions.clear();
            return this;
        }

        public Builder setPositions(Map<String, Pair<Integer, Integer>> positions) {
            this.positions.clear();
            this.positions.putAll(positions);
            return this;
        }

        public Builder setEnchants(Map<String, ConfigItem.EnchantData> enchants) {
            this.enchants.clear();
            this.enchants.putAll(enchants);
            return this;
        }

        public Builder clearEnchants() {
            this.enchants.clear();
            return this;
        }

        public Builder noPermission() {
            this.requiresPermission = false;
            return this;
        }

        public Builder requiresPermission(
                String permission,
                ConfigItem elseItem) {
            this.requiresPermission = true;
            this.permission = permission;
            this.elseItem = elseItem;
            return this;
        }

        public Builder remainOpenOnClick() {
            this.closeOnClick = false;
            return this;
        }

        public Builder closeOnClick() {
            this.closeOnClick = true;
            return this;
        }

        public Builder executeCommands(String... commands) {
            this.commandsExecuted.addAll(List.of(commands));
            return this;
        }

        public Builder dataComponents(CommentedConfigurationNode components) {
            this.components = components;
            return this;
        }

        public ExtendedConfigItem build() {
            return new ExtendedConfigItem(
                    this.enabled, this.type, this.amount,
                    this.name, this.flags, this.lore, this.enchants,
                    this.nbt, this.positions, this.requiresPermission,
                    this.permission, this.elseItem, this.closeOnClick,
                    this.commandsExecuted, this.components);
        }
    }

    public static class InternalConfigItem extends ConfigItem {

        private final ExtendedConfigItem parent;

        public InternalConfigItem(ExtendedConfigItem parent) {
            this.parent = parent;
        }

        @Override
        public <T> Displayable.Builder<T> toDisplayableBuilder(Placeholder... placeholders) {
            return GuiFactory.convertConfigItemBuilder(this, placeholders);
        }

        @Override
        public Displayable toDisplayable(Placeholder... placeholders) {
            return GuiFactory.convertConfigItem(this, placeholders);
        }

        @Override
        public Map<String, NBTValue> getNbt() {
            return parent.getNbt();
        }

        @Override
        public List<String> getFlags() {
            return parent.getFlags();
        }

        @Override
        public Map<String, EnchantData> getEnchants() {
            return parent.getEnchants();
        }

        @Override
        public List<String> getLore() {
            return parent.getLore();
        }

        @Override
        public String getName() {
            return parent.getName();
        }

        @Override
        public int getAmount(List<Placeholder> placeholders) {
            return parent.getAmount(placeholders);
        }

        @Override
        public int getAmount() {
            return parent.getAmount();
        }

        @Override
        public String getType() {
            return parent.getType();
        }

        @Override
        public boolean isEnabled() {
            return parent.isEnabled();
        }

        @Override
        public CommentedConfigurationNode getComponents() {
            return this.parent.components;
        }
    }
}
