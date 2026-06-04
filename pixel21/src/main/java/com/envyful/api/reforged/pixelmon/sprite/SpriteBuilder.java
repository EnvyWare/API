package com.envyful.api.reforged.pixelmon.sprite;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBall;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import com.pixelmonmod.pixelmon.init.registry.PixelmonDataComponents;
import com.pixelmonmod.pixelmon.items.SpriteItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 *
 * Builder class for creating a Pixelmon sprite ItemStack
 *
 */
public class SpriteBuilder {

    private Species species;
    private Gender gender = Gender.MALE;
    private String palette = "none";
    private int eggCycles = -1;

    private String nick = null;
    private Stats form = null;

    public SpriteBuilder() {}

    public SpriteBuilder species(Species species) {
        this.species = species;
        return this;
    }

    public SpriteBuilder gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public SpriteBuilder eggCycles(int eggCycles) {
        this.eggCycles = eggCycles;
        return this;
    }

    public SpriteBuilder shiny() {
        return this.palette("shiny");
    }

    public SpriteBuilder palette(String palette) {
        this.palette = palette;
        return this;
    }

    public SpriteBuilder nick(String nick) {
        this.nick = nick;
        return this;
    }

    public SpriteBuilder form(Stats form) {
        this.form = form;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(ItemRegistration.PIXELMON_SPRITE.get());

        itemStack.set(PixelmonDataComponents.POKEMON_BASE, new PokemonBase(species.getDex(), form.getName(), gender, palette, eggCycles, (PokeBall) PokeBallRegistry.POKE_BALL.getValueUnsafe()));
        if (this.nick != null && !this.nick.isEmpty()) {
            itemStack.set(PixelmonDataComponents.NICKNAME, this.nick);
        }

        return itemStack;
    }
}
