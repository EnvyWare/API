package com.envyful.api.reforged;

import com.envyful.api.InitializationTask;
import com.envyful.api.config.ConfigTypeSerializer;
import com.envyful.api.reforged.battle.BattleRulesConfigRegistry;
import com.envyful.api.reforged.config.PokemonSpecSerializer;
import com.pixelmonmod.api.pokemon.PokemonSpecification;

/**
 *
 * Task that runs on server startup to register the PokemonSpecSerializer before the configs are loaded
 * by the plugin or mod
 *
 */
public class Init implements InitializationTask {

    @Override
    public void run() {
        BattleRulesConfigRegistry.init();
        ConfigTypeSerializer.register(PokemonSpecSerializer.getInstance(), PokemonSpecification.class);
    }
}
