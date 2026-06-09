package com.envyful.api.config.type;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.UtilParse;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.*;

/**
 *
 * A serializable object that can be used to represent an Item in a config
 *
 */
@ConfigSerializable
public class ConfigItem {

    private boolean enabled = true;
    private String type = "minecraft:black_stained_glass_pane";
    private String amount = "1";
    private String name = " ";
    private List<String> flags = new ArrayList<>();
    private List<String> lore = new ArrayList<>();
    private Map<String, EnchantData> enchants = new HashMap<>();
    private Map<String, NBTValue> nbt = new HashMap<>();
    private CommentedConfigurationNode components = CommentedConfigurationNode.root();

    public ConfigItem() {}

    ConfigItem(Builder builder) {
        this.enabled = builder.enabled;
        this.type = builder.type;
        this.amount = builder.amount;
        this.name = builder.name;
        this.flags = builder.flags;
        this.lore = builder.lore;
        this.enchants = builder.enchants;
        this.nbt = builder.nbt;
        this.components = builder.components;
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

    public Map<String, EnchantData> getEnchants() {
        return enchants;
    }

    public List<String> getFlags() {
        return flags;
    }

    public Map<String, NBTValue> getNbt() {
        return this.nbt;
    }

    public CommentedConfigurationNode getComponents() {
        return this.components;
    }

    public Displayable toDisplayable(Placeholder... placeholders) {
        return GuiFactory.convertConfigItem(this, placeholders);
    }

    public <T> Displayable.Builder<T> toDisplayableBuilder(Placeholder... placeholders) {
        return GuiFactory.convertConfigItemBuilder(this, placeholders);
    }

    public boolean matches(Object itemStack) {
        return PlatformProxy.isItem(itemStack, this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setEnchants(Map<String, EnchantData> enchants) {
        this.enchants = enchants;
    }

    public void setNbt(Map<String, NBTValue> nbt) {
        this.nbt = nbt;
    }

    public void setComponents(CommentedConfigurationNode components) {
        this.components = components;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ConfigItem configItem) {
        return new Builder()
                .type(configItem.type)
                .amount(configItem.getAmount())
                .lore(configItem.lore.toArray(new String[0]))
                .name(configItem.name)
                .flags(configItem.flags.toArray(new String[0]))
                .enchants(configItem.enchants.values().toArray(new EnchantData[0]))
                .dataComponents(configItem.components);
    }

    @ConfigSerializable
    public static final class NBTValue {

        private String type;
        private String data;
        private Map<String, NBTValue> subData;

        public NBTValue() {}

        public NBTValue(String type, String data) {
            this.type = type;
            this.data = data;
        }

        public NBTValue(String type, Map<String, NBTValue> subData) {
            this.type = type;
            this.subData = subData;
        }

        public String getType() {
            return this.type;
        }

        public String getData() {
            return this.data;
        }

        public Map<String, NBTValue> getSubData() {
            return subData;
        }
    }

    @ConfigSerializable
    public static final class EnchantData {

        private String enchant;
        private String level;

        public EnchantData() {}

        public EnchantData(String enchant, String level) {
            this.enchant = enchant;
            this.level = level;
        }

        public String getEnchant() {
            return this.enchant;
        }

        public String getLevel() {
            return this.level;
        }
    }

    public static class Builder {

        private boolean enabled = true;
        private String type = "minecraft:black_stained_glass_pane";
        private String amount = "1";
        private String name = " ";
        private List<String> flags = new ArrayList<>();
        private List<String> lore = new ArrayList<>();
        private Map<String, EnchantData> enchants = new HashMap<>();
        private Map<String, NBTValue> nbt = new HashMap<>();
        private CommentedConfigurationNode components = CommentedConfigurationNode.root();

        protected Builder() {}

        public Builder enabled() {
            this.enabled = true;
            return this;
        }

        public Builder disabled() {
            this.enabled = false;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = String.valueOf(amount);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder flags(String... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public Builder clearLore() {
            this.lore.clear();
            return this;
        }

        public Builder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder setLore(String... lore) {
            this.lore = new ArrayList<>();
            this.lore.addAll(List.of(lore));
            return this;
        }

        public Builder lore(String... lore) {
            this.lore.addAll(List.of(lore));
            return this;
        }

        public Builder enchants(EnchantData... enchants) {
            for (EnchantData enchant : enchants) {
                this.enchants.put("enchant-" + this.enchants.size(), enchant);
            }

            return this;
        }

        public Builder nbt(String key, NBTValue value) {
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

        public Builder dataComponents(CommentedConfigurationNode components) {
            this.components = components;
            return this;
        }

        public ConfigItem build() {
            return new ConfigItem(this);
        }
    }
}
