package com.envyful.api.reforged.pixelmon;

import com.google.common.collect.Lists;
import com.mc.blaze.core.utils.item.UtilItemStack;
import com.mc.blaze.core.utils.pixelmon.requirement.Requirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Objects;

public class PokemonSpec {

    private EnumSpecies species = null;
    private boolean allowEvolutions = false;
    private Gender gender = null;
    private List<EnumNature> natures = Lists.newArrayList();
    private List<EnumGrowth> growths = Lists.newArrayList();
    private Requirement<Integer> ivRequirement = null;
    private List<String> description = null;

    public PokemonSpec() {}

    public String getName() {
        return this.species.name;
    }

    public void setSpecies(EnumSpecies species) {
        this.species = species;
    }

    public void setAllowEvolutions(boolean allowEvolutions) {
        this.allowEvolutions = allowEvolutions;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void addNature(EnumNature nature) {
        this.natures.add(nature);
    }

    public void addGrowth(EnumGrowth growth) {
        this.growths.add(growth);
    }

    public void setIVRequirement(Requirement<Integer> requirement) {
        this.ivRequirement = requirement;
    }

    public boolean matches(EntityPixelmon pixelmon) {
        return this.matches(pixelmon.getPokemonData());
    }

    public boolean matches(Pokemon pokemon) {
        if (!this.doesSpeciesMatch(pokemon)) {
            return false;
        }

        if (this.gender != null && !Objects.equals(this.gender, pokemon.getGender())) {
            return false;
        }

        if (!this.doesGrowthMatch(pokemon)) {
            return false;
        }

        if (!this.doesNatureMatch(pokemon)) {
            return false;
        }

        if (this.ivRequirement == null) {
            return true;
        }

        return this.ivRequirement.fits((int) pokemon.getIVs().getPercentage(1));
    }

    private boolean doesSpeciesMatch(Pokemon pokemon) {
        if (this.species == null) {
            return true;
        }

        if (this.allowEvolutions) {
            if (!Objects.equals(this.species, pokemon.getSpecies())) {
                for (String preEvolution : pokemon.getBaseStats().preEvolutions) {
                    if (preEvolution.equals(pokemon.getSpecies().name)) {
                        return true;
                    }
                }
            }
        }

        return Objects.equals(this.species, pokemon.getSpecies());
    }

    private boolean doesNatureMatch(Pokemon pokemon) {
        if (this.natures.isEmpty()) {
            return true;
        }

        return this.natures.contains(pokemon.getNature());
    }

    private boolean doesGrowthMatch(Pokemon pokemon) {
        if (this.growths.isEmpty()) {
            return true;
        }

        return this.growths.contains(pokemon.getGrowth());
    }

    public ItemStack getPhoto() {
        ItemStack itemStack = new ItemStack(PixelmonItems.itemPixelmonSprite);
        NBTTagCompound tagCompound = new NBTTagCompound();

        itemStack.setTagCompound(tagCompound);
        tagCompound.setShort("ndex", (short) this.species.getNationalPokedexInteger());

        itemStack.setStackDisplayName("§eHunting for §6§l" + this.species.getPokemonName());
        UtilItemStack.setLore(itemStack, this.getDescription());

        return itemStack;
    }

    private List<String> getDescription() {
        if (this.description == null) {
            this.description = description = Lists.newArrayList(
                    "",
                    "§fRequirements:"
            );

            if (this.allowEvolutions) {
                this.description.add("§7• Evolutions are allowed");
            } else {
                this.description.add("§7• Evolutions are not allowed");
            }

            if (this.gender != null) {
                this.description.add("§7• " + this.gender.name() + " Gender");
            }

            if (!this.natures.isEmpty()) {
                this.description.add("§7• Natures: ");

                for (EnumNature nature : this.natures) {
                    this.description.add("  §7• " + nature.getName());
                }
            }

            if (!this.growths.isEmpty()) {
                this.description.add("§7• Growths: ");

                for (EnumGrowth growth : this.growths) {
                    this.description.add("  §7• " + growth.name());
                }
            }

            if (this.ivRequirement != null) {
                this.description.add("§7• IV Requirement: " + this.ivRequirement.get() + "%");
            }
        }

        return description;
    }

    @Override
    public String toString() {
        return "PokemonSpec{" +
                "species=" + species +
                ", allowEvolutions=" + allowEvolutions +
                ", gender=" + gender +
                ", natures=" + natures +
                ", growths=" + growths +
                ", ivRequirement=" + ivRequirement +
                ", description=" + description +
                '}';
    }
}
