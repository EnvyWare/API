package com.envyful.api.reforged.config;

import com.envyful.api.InitializationTask;
import com.envyful.api.config.ConfigTypeSerializer;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PokemonSpecSerializer implements TypeSerializer<PokemonSpecification>, InitializationTask {

    private static final PokemonSpecSerializer INSTANCE = new PokemonSpecSerializer();

    public PokemonSpecSerializer() {}

    public static PokemonSpecSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public PokemonSpecification deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var parseAttempt = PokemonSpecificationProxy.create(node.getString());

        if (parseAttempt.wasError()) {
            throw new SerializationException("Invalid spec provided: '" + parseAttempt.getError() + "' for spec '" + node.getString() + "'");
        }

        return parseAttempt.get();
    }

    @Override
    public void serialize(Type type, @Nullable PokemonSpecification obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(obj.toString());
    }

    @Override
    public void run() {
        ConfigTypeSerializer.register(this, PokemonSpecification.class);
    }
}
