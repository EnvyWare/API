package com.envyful.api.forge.items;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.Collections;
import java.util.List;

/**
 *
 * Static utility class for generic {@link ItemStack} methods.
 *
 */
public class UtilItemStack {

    /**
     *
     * Returns the lore of the {@param itemStack} as a {@link List} of Strings.
     *
     * Will return {@link Collections#emptyList()} if the parameter is null
     *
     * @param itemStack The item to get the lore of
     * @return The lore of the item
     */
    public static List<String> getLore(ItemStack itemStack) {
        if (itemStack == null) {
            return Collections.emptyList();
        }

        List<String> lore = Lists.newArrayList();

        NBTTagList currentLore = itemStack.getOrCreateSubCompound("display").getTagList("Lore", 8);

        for (NBTBase nbtBase : currentLore) {
            if (nbtBase instanceof NBTTagString) {
                lore.add(((NBTTagString) nbtBase).getString());
            }
        }

        return lore;
    }

    /**
     *
     * Sets the lore of the specified itemstack to the given list
     *
     * @param itemStack The itemstack to update the lore of
     * @param lore The new lore
     */
    public static void setLore(ItemStack itemStack, List<String> lore) {
        NBTTagCompound display = itemStack.getOrCreateSubCompound("display");
        NBTTagList newLore = new NBTTagList();

        lore.forEach(s -> newLore.appendTag(new NBTTagString(s)));

        display.setTag("Lore", newLore);
        itemStack.setTagInfo("display", display);
    }
}
