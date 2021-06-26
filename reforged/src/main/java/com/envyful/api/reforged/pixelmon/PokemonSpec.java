package com.envyful.api.reforged.pixelmon;

import com.envyful.api.forge.items.UtilItemStack;
import com.envyful.api.type.requirement.Requirement;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * POJO for handling pokemon details and then determining if a given entity matches the details in the specification.
 *
 */
public class PokemonSpec {

    protected final EnumSpecies species;
    protected final boolean allowEvolutions;
    protected final Gender gender;
    protected final Requirement<Integer> ivRequirement;
    protected final List<EnumNature> natures;
    protected final List<EnumGrowth> growths;

    private PokemonSpec(EnumSpecies species, boolean allowEvolutions, Gender gender, Requirement<Integer> ivRequirement,
                        List<EnumNature> natures, List<EnumGrowth> growths) {
        this.species = species;
        this.allowEvolutions = allowEvolutions;
        this.gender = gender;
        this.ivRequirement = ivRequirement;
        this.natures = natures;
        this.growths = growths;
    }

    /**
     *
     * Method used to determine if the entity passed matches this pokemon specification
     *
     * @param pixelmon The entity being checked
     * @return True if it matches - false otherwise
     */
    public boolean matches(EntityPixelmon pixelmon) {
        return this.matches(pixelmon.getPokemonData());
    }

    /**
     *
     * Method used to determine if the pokemon passed matches this pokemon specification
     *
     * @param pokemon The pokemon being checked
     * @return True if it matches - false otherwise
     */
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

    @Override
    public String toString() {
        return "PokemonSpec{" +
                "species=" + species +
                ", allowEvolutions=" + allowEvolutions +
                ", gender=" + gender +
                ", natures=" + natures +
                ", growths=" + growths +
                ", ivRequirement=" + ivRequirement +
                '}';
    }

    /**
     *
     * Static factory method for getting the builder
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private EnumSpecies species = null;
        private boolean allowEvolutions = false;
        private Gender gender = null;
        private Requirement<Integer> ivRequirement = null;
        private List<EnumNature> natures = Lists.newArrayList();
        private List<EnumGrowth> growths = Lists.newArrayList();

        private Builder() {}

        public Builder species(EnumSpecies species) {
            this.species = species;
            return this;
        }

        public Builder allowEvolutions(boolean allowEvolutions) {
            this.allowEvolutions = allowEvolutions;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder ivRequirement(Requirement<Integer> ivRequirement) {
            this.ivRequirement = ivRequirement;
            return this;
        }

        public Builder nature(EnumNature nature) {
            this.natures.add(nature);
            return this;
        }

        public Builder natures(EnumNature... nature) {
            this.natures.addAll(Arrays.asList(nature));
            return this;
        }

        public Builder growth(EnumGrowth growth) {
            this.growths.add(growth);
            return this;
        }

        public Builder growths(EnumGrowth... growths) {
            this.growths.addAll(Arrays.asList(growths));
            return this;
        }

        public PokemonSpec build() {
            return new PokemonSpec(this.species, this.allowEvolutions, this.gender, this.ivRequirement, this.natures,
                    this.growths);
        }
    }

    public static class Display {

        protected final PokemonSpec spec;

        private List<String> description = null;

        public Display(PokemonSpec spec) {
            this.spec = spec;
        }

        public ItemStack getPhoto() {
            ItemStack itemStack = new ItemStack(PixelmonItems.itemPixelmonSprite);
            NBTTagCompound tagCompound = new NBTTagCompound();

            itemStack.setTagCompound(tagCompound);
            tagCompound.setShort("ndex", (short) this.spec.species.getNationalPokedexInteger());

            itemStack.setStackDisplayName("§eHunting for §6§l" + this.spec.species.getPokemonName());
            UtilItemStack.setLore(itemStack, this.getDescription());

            return itemStack;
        }

        private List<String> getDescription() {
            if (this.description == null) {
                this.description = Lists.newArrayList(
                        "",
                        "§fRequirements:"
                );

                if (this.spec.allowEvolutions) {
                    this.description.add("§7• Evolutions are allowed");
                } else {
                    this.description.add("§7• Evolutions are not allowed");
                }

                if (this.spec.gender != null) {
                    this.description.add("§7• " + this.spec.gender.name() + " Gender");
                }

                if (!this.spec.natures.isEmpty()) {
                    this.description.add("§7• Natures: ");

                    for (EnumNature nature : this.spec.natures) {
                        this.description.add("  §7• " + nature.getName());
                    }
                }

                if (!this.spec.growths.isEmpty()) {
                    this.description.add("§7• Growths: ");

                    for (EnumGrowth growth : this.spec.growths) {
                        this.description.add("  §7• " + growth.name());
                    }
                }

                if (this.spec.ivRequirement != null) {
                    this.description.add("§7• IV Requirement: " + this.spec.ivRequirement.get() + "%");
                }
            }

            return description;
        }
    }
}
