package com.envyful.api.neoforge.items;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.platform.PlatformProxy;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Forge {@link ItemStack} builder class. Allows specifying {@link ItemFlag}s, unbreakability, nbt data, enchants (etc).
 *
 */
public class ItemBuilder implements Cloneable {

    private Item type = Items.AIR;
    private int amount = 1;
    private Component name = null;
    private boolean unbreakable = false;
    private List<Component> lore = new ArrayList<>();
    private List<ItemFlag> itemFlags = new ArrayList<>();
    private Map<String, Tag> customData = new HashMap<>();
    private Map<Holder<Enchantment>, Integer> enchants = new HashMap<>();
    private PatchedDataComponentMap dataComponents = new PatchedDataComponentMap(DataComponentMap.EMPTY);

    /**
     *
     * Basic constructor providing empty item builder with no specific values to begin with.
     *
     */
    public ItemBuilder() {}

    /**
     *
     * Converts {@link ItemStack} to builder
     *
     * @param itemStack The itemstack being converted
     */
    public ItemBuilder(ItemStack itemStack) {
        this.type = itemStack.getItem();
        this.amount = itemStack.getCount();
        this.name = itemStack.getHoverName();
        this.lore = new ArrayList<>(UtilItemStack.getRealLore(itemStack));
        this.dataComponents.setAll(itemStack.getComponents());
    }

    /**
     *
     * Sets the type of item that the itemstack will be
     *
     * @param type The minecraft type of the item
     * @return The builder
     */
    public ItemBuilder type(Item type) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid item provided");
        }

        this.type = type;
        return this;
    }

    /**
     *
     * Sets the amount of the item in the itemstack
     *
     * @param amount The amount
     * @return The builder
     */
    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     *
     * Sets the new name of the item
     *
     * @param name The new name
     * @return The builder
     */
    public ItemBuilder name(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            this.name = null;
            return this;
        }

        return this.name(PlatformProxy.<Component>parse(name));
    }

    /**
     *
     * Sets the new name of the item
     *
     * @param name The new name
     * @return The builder
     */
    public ItemBuilder name(Component name) {
        this.name = name;
        return this;
    }

    /**
     *
     * Sets the list of the strings as the stored lore (doesn't ADD to the lore)
     *
     * @param lore The new lore for the item
     * @return The builder
     */
    public ItemBuilder lore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    /**
     *
     * Sets the array of the strings as the stored lore (doesn't ADD to the lore)
     *
     * @param lore The new lore for the item
     * @return The builder
     */
    public ItemBuilder lore(String... lore) {
        this.lore = Arrays.stream(lore).map(Component::literal).collect(Collectors.toList());
        return this;
    }

    /**
     *
     * Sets the array of the ITextComponent as the stored lore (doesn't ADD to the lore)
     *
     * @param lore The new lore for the item
     * @return The builder
     */
    public ItemBuilder lore(Component... lore) {
        this.lore = new ArrayList<>();
        this.lore.addAll(List.of(lore));
        return this;
    }


    /**
     *
     * Adds the array of Strings to the stored lore (doesn't SET the lore)
     *
     * @param lore The lines to add to the lore
     * @return The builder
     */
    public ItemBuilder addLore(String... lore) {
        return this.addLore(Arrays.stream(lore).map(Component::literal).toArray(Component[]::new));
    }

    /**
     *
     * Adds the array of {@link Component} to the stored lore (doesn't SET the lore)
     *
     * @param lore The lines to add to the lore
     * @return The builder
     */
    public ItemBuilder addLore(Component... lore) {
        this.lore.addAll(List.of(lore));
        return this;
    }

    /**
     *
     * Sets the NBT base value at the key given
     *
     * @param key The key to set the value at
     * @param primitive The value to add under the given key
     * @return The builder
     */
    public ItemBuilder nbt(String key, Tag primitive) {
        this.customData.put(key, primitive);
        return this;
    }

    /**
     *
     * Sets if the item built should be breakable
     *
     * @param unbreakable true = unbreakable, false = breakable
     * @return The builder
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     *
     * Adds the specified {@link ItemFlag} to the builder class
     *
     * @param itemFlag The item flag to add
     * @return The builder
     */
    public ItemBuilder itemFlag(ItemFlag itemFlag) {
        this.itemFlags.add(itemFlag);
        return this;
    }

    /**
     *
     * Adds the specified {@link ItemFlag}s to the builder class
     *
     * @param itemFlags The item flags to add
     * @return The builder
     */
    public ItemBuilder itemFlags(Collection<ItemFlag> itemFlags) {
        this.itemFlags.addAll(itemFlags);
        return this;
    }

    /**
     *
     * Adds the specified {@link ItemFlag}s to the builder class
     *
     * @param itemFlags The item flags to add
     * @return The builder
     */
    public ItemBuilder itemFlags(ItemFlag... itemFlags) {
        this.itemFlags.addAll(Arrays.asList(itemFlags));
        return this;
    }

    /**
     *
     * Enchants the item with the specified enchant and the given level.
     *
     * @param enchantment The enchantment to add to the builder
     * @param level The level of the enchant
     * @return The builder
     */
    public ItemBuilder enchant(Holder<Enchantment> enchantment, int level) {
        this.enchants.put(enchantment, level);
        return this;
    }

    public ItemBuilder setComponents(DataComponentMap dataComponents) {
        this.dataComponents = new PatchedDataComponentMap(dataComponents);
        return this;
    }

    /**
     *
     * Gets the NBT tag for the specified key.
     *
     * Will return null if the value found at the key is not a tag compound.
     * However, will also insert a new tag compound (and return it) if it wasn't already there.
     *
     * @param key The key to be checked
     * @return The NBT tag at that key
     */
    public CompoundTag getCompound(String key) {
        Tag nbtBase = this.customData.computeIfAbsent(key, ___ -> new CompoundTag());

        if (!(nbtBase instanceof CompoundTag)) {
            return null;
        }

        return (CompoundTag) nbtBase;
    }

    /**
     *
     * Method to turn the {@link ItemBuilder} to a new forge {@link ItemStack} instance
     *
     * @return The forge item
     */
    public ItemStack build() {
        if (this.type == null) {
            throw new IllegalArgumentException("Invalid item provided");
        }

        ItemStack itemStack = new ItemStack(this.type, this.amount);
        itemStack.applyComponents(this.dataComponents);

        if (!customData.isEmpty()) {
            CompoundTag compound = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            for (Map.Entry<String, Tag> entry : customData.entrySet()) {
                compound.put(entry.getKey(), entry.getValue());
            }

            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        }

        if (this.name != null && !this.name.getString().isEmpty()) {
            itemStack.set(DataComponents.CUSTOM_NAME, this.name);
        }

        if (this.lore != null && !this.lore.isEmpty()) {
            itemStack.set(DataComponents.LORE, new ItemLore(this.lore, this.lore));
        }

        if (this.unbreakable) {
            itemStack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
        }

        for (var entry : enchants.entrySet()) {
            itemStack.enchant(entry.getKey(), entry.getValue());
        }

        if (this.itemFlags != null && !this.itemFlags.isEmpty()) {
            for (ItemFlag itemFlag : this.itemFlags) {
                itemFlag.apply(itemStack);
            }
        }

        return itemStack;
    }

    /**
     *
     * Method to create a copy of the ItemBuilder in a new instance
     *
     * @return The new item builder that the parameters have been copied to
     */
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ItemBuilder clone() {
        ItemBuilder copy = new ItemBuilder();
        copy.type(this.type);
        copy.name(this.name);
        copy.amount(this.amount);
        copy.lore(this.lore);
        return copy;
    }
}
