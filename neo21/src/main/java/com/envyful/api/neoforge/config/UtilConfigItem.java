package com.envyful.api.neoforge.config;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.neoforge.config.yaml.YamlOps;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.items.ItemFlag;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.Pair;
import com.envyful.api.type.UtilParse;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.*;

public class UtilConfigItem {

    private UtilConfigItem() {
        throw new UnsupportedOperationException("Static utility class");
    }

    @Deprecated(since = "7.6.7", forRemoval = true)
    public static ConfigItemBuilder builder() {
        return new ConfigItemBuilder();
    }

    public static ItemStack fromPermissibleItem(ForgeEnvyPlayer player, ExtendedConfigItem permissibleConfigItem, Placeholder... transformers) {
        return fromPermissibleItem(player, permissibleConfigItem, List.of(transformers));
    }

    public static ItemStack fromPermissibleItem(ForgeEnvyPlayer player, ExtendedConfigItem permissibleConfigItem, List<Placeholder> transformers) {
        if (!permissibleConfigItem.isEnabled()) {
            return null;
        }

        if (permissibleConfigItem.hasPermission(player)) {
            return fromConfigItem(permissibleConfigItem, transformers);
        }

        if (permissibleConfigItem.getElseItem() == null || !permissibleConfigItem.getElseItem().isEnabled()) {
            return null;
        }

        return fromConfigItem(permissibleConfigItem.getElseItem(), transformers);
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, Placeholder... transformers) {
        return fromConfigItem(configItem.asConfigItem(), List.of(transformers));
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, List<Placeholder> transformers) {
        return fromConfigItem(configItem.asConfigItem(), transformers);
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, Placeholder... transformers) {
        return fromConfigItem(configItem, List.of(transformers));
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, List<Placeholder> placeholders) {
        if (!configItem.isEnabled()) {
            return null;
        }

        var name = configItem.getName();
        var type = PlaceholderFactory.handlePlaceholders(configItem.getType(), placeholders);

        if (type.isEmpty()) {
            UtilLogger.getLogger().error("Invalid type provided for config item: {}", configItem.getType());
            return null;
        }

        var itemBuilder = new ItemBuilder()
                .type(fromNameOrId(type.getFirst()))
                .amount(configItem.getAmount(placeholders));

        itemBuilder.lore(PlaceholderFactory.handlePlaceholders(configItem.getLore(), PlatformProxy::<Component>parse, placeholders));
        itemBuilder.itemFlags(PlaceholderFactory.handlePlaceholders(configItem.getFlags(), s -> ItemFlag.valueOf(s.toUpperCase(Locale.ROOT)), placeholders));

        if (!name.isEmpty()) {
            itemBuilder.name(PlaceholderFactory.handlePlaceholders(Collections.singletonList(name), PlatformProxy::<Component>parse, placeholders).get(0));
        }

        for (var value : configItem.getEnchants().values()) {
            String enchantName = value.getEnchant();
            String level = value.getLevel();

            if (!placeholders.isEmpty()) {
                enchantName = PlaceholderFactory.handlePlaceholders(Collections.singletonList(enchantName), placeholders).get(0);
                level = PlaceholderFactory.handlePlaceholders(Collections.singletonList(level), placeholders).get(0);
            }

            var registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            var enchantment = registry.getHolder(ResourceLocation.tryParse(enchantName.toLowerCase())).orElse(null);
            int parsedLevel = UtilParse.parseInt(level).orElse(1);

            if (enchantment == null) {
                UtilLogger.getLogger().error("Invalid enchantment provided: {}", enchantName);
                continue;
            }

            itemBuilder.enchant(enchantment, parsedLevel);
        }

        for (Map.Entry<String, ConfigItem.NBTValue> nbtData : configItem.getNbt().entrySet()) {
            Pair<String, Tag> parsed = parseNBT(nbtData, placeholders);

            if (parsed != null) {
                itemBuilder.nbt(parsed.getX(), parsed.getY());
            }
        }

        if (configItem.getComponents() != null && !Objects.equals(configItem.getComponents(), CommentedConfigurationNode.root())) {
            var registries = ServerLifecycleHooks.getCurrentServer().registryAccess();
            var ops = RegistryOps.create(YamlOps.INSTANCE, registries);
            var dataComponents = DataComponentMap.CODEC.decode(ops, configItem.getComponents()).getOrThrow().getFirst();
            itemBuilder.setComponents(dataComponents);
        }

        return itemBuilder.build();
    }

    public static Pair<String, Tag> parseNBT(Map.Entry<String, ConfigItem.NBTValue> nbtEntry, List<Placeholder> placeholders) {
        if (nbtEntry.getValue().getType().equalsIgnoreCase("nbt")) {
            CompoundTag compound = new CompoundTag();

            for (Map.Entry<String, ConfigItem.NBTValue> entry : nbtEntry.getValue().getSubData().entrySet()) {
                Pair<String, Tag> parsed = parseNBT(entry, placeholders);

                if (parsed != null) {
                    compound.put(parsed.getX(), parsed.getY());
                }
            }

            return Pair.of(nbtEntry.getKey(), compound);
        }

        if (nbtEntry.getValue().getType().equalsIgnoreCase("list")) {
            ListTag list = new ListTag();

            for (Map.Entry<String, ConfigItem.NBTValue> nbtValue : nbtEntry.getValue().getSubData().entrySet()) {
                Pair<String, Tag> parsed = parseNBT(nbtValue, placeholders);

                if (parsed != null) {
                    CompoundTag compound = new CompoundTag();
                    compound.put(parsed.getX(), parsed.getY());
                    list.add(compound);
                }
            }

            return Pair.of(nbtEntry.getKey(), list);
        }

        return Pair.of(nbtEntry.getKey(), parseBasic(nbtEntry.getValue(), placeholders));
    }

    public static Tag parseBasic(ConfigItem.NBTValue value, List<Placeholder> placeholders) {
        String data = value.getData();

        if (!placeholders.isEmpty()) {
            data = PlaceholderFactory.handlePlaceholders(Collections.singletonList(data), placeholders).get(0);
        }

        Tag base;

        switch (value.getType().toLowerCase()) {
            case "int":
            case "integer":
                base = IntTag.valueOf(Integer.parseInt(data));
                break;
            case "long":
                base = LongTag.valueOf(Long.parseLong(data));
                break;
            case "byte":
                base = ByteTag.valueOf(Byte.parseByte(data));
                break;
            case "double":
                base = DoubleTag.valueOf(Double.parseDouble(data));
                break;
            case "float":
                base = FloatTag.valueOf(Float.parseFloat(data));
                break;
            case "short":
                base = ShortTag.valueOf(Short.parseShort(data));
                break;
            default:
            case "string":
                base = StringTag.valueOf(data);
                break;
        }

        return base;
    }

    public static Item fromNameOrId(String data) {
        try {
            var registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.ITEM);
            var item = registry.getOptional(ResourceLocation.tryParse(data)).orElse(null);

            if (item != null) {
                return item;
            }

            int integer = UtilParse.parseInt(data).orElse(-1);

            if (integer == -1) {
                throw new IllegalArgumentException("Invalid item type provided: " + data);
            }

            return Item.byId(integer);
        } catch (ResourceLocationException e) {
            throw new IllegalArgumentException("Invalid item type provided: " + data, e);
        }
    }

}
