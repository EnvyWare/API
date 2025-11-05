package com.envyful.api.neoforge.config;

import com.envyful.api.neoforge.config.yaml.YamlOps;
import com.mojang.serialization.Codec;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CodecTypeSerializer<T> implements TypeSerializer<T> {

    private final Codec<T> codec;

    public CodecTypeSerializer(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return this.codec.decode(RegistryOps.create(YamlOps.INSTANCE, ServerLifecycleHooks.getCurrentServer().registryAccess()), (CommentedConfigurationNode) node).getOrThrow(s -> new IllegalArgumentException("Failed to decode ItemStack " + s)).getFirst();
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        var result = this.codec.encode(obj, RegistryOps.create(YamlOps.INSTANCE, ServerLifecycleHooks.getCurrentServer().registryAccess()), CommentedConfigurationNode.root()).getOrThrow();
        node.set(result);
    }
}
