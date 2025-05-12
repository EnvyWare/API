package com.envyful.api.reforged.pixelmon.transformer;

import com.envyful.api.text.parse.SimplePlaceholder;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;

/**
 *
 * Transformer that replaces %pokedex_formatted% with the formatted dex number of the Pokemon
 *
 */
public class PokemonDexFormattedTransformer implements SimplePlaceholder {

    private final String ndex;

    public static PokemonDexFormattedTransformer of(Pokemon pokemon) {
        return of(pokemon.getSpecies());
    }

    public static PokemonDexFormattedTransformer of(Species species) {
        return new PokemonDexFormattedTransformer(species.getFormattedDex());
    }

    private PokemonDexFormattedTransformer(String ndex) {this.ndex = ndex;}

    @Override
    public String replace(String name) {
        return name.replace("%pokedex_formatted%", this.ndex);
    }
}
