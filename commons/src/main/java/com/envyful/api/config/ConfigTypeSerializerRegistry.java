package com.envyful.api.config;

import java.util.*;

/**
 *
 * Static factory class for registering and retrieving {@link ConfigTypeSerializer} instances
 *
 */
public class ConfigTypeSerializerRegistry {

    private static final Map<Class<?>, ConfigTypeSerializer<?>> REGISTRY = new HashMap<>();

    public static void register(ConfigTypeSerializer<?> serializer) {
        Objects.requireNonNull(serializer, "serializer");
        REGISTRY.put(serializer.clazz(), serializer);
    }

    public static ConfigTypeSerializer<?> get(Class<?> id) {
        return REGISTRY.get(id);
    }

    public static List<ConfigTypeSerializer<?>> getAll() {
        if (REGISTRY.isEmpty()) {
            return List.of();
        }

        return new ArrayList<>(REGISTRY.values());
    }
}
