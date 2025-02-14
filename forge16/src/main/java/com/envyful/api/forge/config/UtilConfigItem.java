package com.envyful.api.forge.config;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.type.Pair;
import com.envyful.api.type.UtilParse;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UtilConfigItem {

    private UtilConfigItem() {
        throw new UnsupportedOperationException("Static utility class");
    }

    public static ConfigItemBuilder builder() {
        return new ConfigItemBuilder();
    }

    public static void addExtendedConfigItem(Pane pane, ForgeEnvyPlayer player, ExtendedConfigItem configItem, Placeholder... placeholders) {
        builder().extendedConfigItem(player, pane, configItem, placeholders);
    }

    public static ItemStack fromPermissibleItem(ServerPlayerEntity player, ExtendedConfigItem permissibleConfigItem, Placeholder... placeholders) {
        return fromPermissibleItem(player, permissibleConfigItem, List.of(placeholders));
    }

    public static ItemStack fromPermissibleItem(ServerPlayerEntity player, ExtendedConfigItem permissibleConfigItem, List<Placeholder> placeholders) {
        if (!permissibleConfigItem.isEnabled()) {
            return null;
        }

        if (hasPermission(player, permissibleConfigItem)) {
            return fromConfigItem(permissibleConfigItem, placeholders);
        }

        if (permissibleConfigItem.getElseItem() == null || !permissibleConfigItem.getElseItem().isEnabled()) {
            return null;
        }

        return fromConfigItem(permissibleConfigItem.getElseItem(), placeholders);
    }

    public static boolean hasPermission(ServerPlayerEntity player, ExtendedConfigItem permissibleConfigItem) {
        return !permissibleConfigItem.requiresPermission() ||
                permissibleConfigItem.getPermission() == null ||
                permissibleConfigItem.getPermission().isEmpty() ||
                permissibleConfigItem.getPermission().equalsIgnoreCase("none") ||
                PlatformProxy.hasPermission(player, permissibleConfigItem.getPermission());
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, Placeholder... placeholders) {
        return fromConfigItem(configItem.asConfigItem(), List.of(placeholders));
    }

    public static ItemStack fromConfigItem(ExtendedConfigItem configItem, List<Placeholder> placeholders) {
        return fromConfigItem(configItem.asConfigItem(), placeholders);
    }

    public static ItemStack fromConfigItem(ConfigItem configItem, Placeholder... placeholders) {
        return fromConfigItem(configItem, List.of(placeholders));
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

        ItemBuilder itemBuilder = new ItemBuilder()
                .type(fromNameOrId(type.get(0)))
                .amount(configItem.getAmount(placeholders));

        itemBuilder.lore(PlaceholderFactory.handlePlaceholders(configItem.getLore(), PlatformProxy::parse, placeholders));
        itemBuilder.itemFlags(PlaceholderFactory.handlePlaceholders(configItem.getFlags(), s -> ItemFlag.valueOf(s.toUpperCase(Locale.ROOT)), placeholders));

        if (!name.isEmpty()) {
            itemBuilder.name(PlaceholderFactory.handlePlaceholders(List.of(name), PlatformProxy::<ITextComponent>parse, placeholders).get(0));
        }

        for (ConfigItem.EnchantData value : configItem.getEnchants().values()) {
            String enchantName = value.getEnchant();
            String level = value.getLevel();

            if (!placeholders.isEmpty()) {
                enchantName = PlaceholderFactory.handlePlaceholders(Collections.singletonList(enchantName), placeholders).get(0);
                level = PlaceholderFactory.handlePlaceholders(Collections.singletonList(level), placeholders).get(0);
            }

            Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(enchantName.toLowerCase())).orElse(null);
            int parsedLevel = UtilParse.parseInt(level).orElse(1);

            if (enchantment == null) {
                continue;
            }

            itemBuilder.enchant(enchantment, parsedLevel);
        }

        for (Map.Entry<String, ConfigItem.NBTValue> nbtData : configItem.getNbt().entrySet()) {
            Pair<String, INBT> parsed = parseNBT(nbtData, placeholders);

            if (parsed != null) {
                itemBuilder.nbt(parsed.getX(), parsed.getY());
            }
        }

        return itemBuilder.build();
    }

    public static Pair<String, INBT> parseNBT(Map.Entry<String, ConfigItem.NBTValue> nbtEntry, List<Placeholder> placeholders) {
        if (nbtEntry.getValue().getType().equalsIgnoreCase("nbt")) {
            CompoundNBT compound = new CompoundNBT();

            for (Map.Entry<String, ConfigItem.NBTValue> entry : nbtEntry.getValue().getSubData().entrySet()) {
                Pair<String, INBT> parsed = parseNBT(entry, placeholders);

                if (parsed != null) {
                    compound.put(parsed.getX(), parsed.getY());
                }
            }

            return Pair.of(nbtEntry.getKey(), compound);
        }

        if (nbtEntry.getValue().getType().equalsIgnoreCase("list")) {
            ListNBT list = new ListNBT();

            for (Map.Entry<String, ConfigItem.NBTValue> nbtValue : nbtEntry.getValue().getSubData().entrySet()) {
                Pair<String, INBT> parsed = parseNBT(nbtValue, placeholders);

                if (parsed != null) {
                    CompoundNBT compound = new CompoundNBT();
                    compound.put(parsed.getX(), parsed.getY());
                    list.add(compound);
                }
            }

            return Pair.of(nbtEntry.getKey(), list);
        }

        return Pair.of(nbtEntry.getKey(), parseBasic(nbtEntry.getValue(), placeholders));
    }

    public static INBT parseBasic(ConfigItem.NBTValue value, List<Placeholder> placeholders) {
        String data = value.getData();

        if (!placeholders.isEmpty()) {
            data = PlaceholderFactory.handlePlaceholders(Collections.singletonList(data), placeholders).get(0);
        }

        INBT base;

        switch (value.getType().toLowerCase()) {
            case "int":
            case "integer":
                base = IntNBT.valueOf(Integer.parseInt(data));
                break;
            case "long":
                base = LongNBT.valueOf(Long.parseLong(data));
                break;
            case "byte":
                base = ByteNBT.valueOf(Byte.parseByte(data));
                break;
            case "double":
                base = DoubleNBT.valueOf(Double.parseDouble(data));
                break;
            case "float":
                base = FloatNBT.valueOf(Float.parseFloat(data));
                break;
            case "short":
                base = ShortNBT.valueOf(Short.parseShort(data));
                break;
            default:
            case "string":
                base = StringNBT.valueOf(data);
                break;
        }

        return base;
    }

    public static Item fromNameOrId(String data) {
        try {
            Item item = Registry.ITEM.getOptional(new ResourceLocation(data)).orElse(null);

            if (item != null) {
                return item;
            }

            int integer = UtilParse.parseInt(data).orElse(-1);

            if (integer == -1) {
                return null;
            }

            return Item.byId(integer);
        } catch (ResourceLocationException e) {
            return null;
        }
    }

}
