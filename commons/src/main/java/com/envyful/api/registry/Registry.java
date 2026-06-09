package com.envyful.api.registry;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.registry.config.KeySerializer;
import com.envyful.api.registry.impl.ClassValueMapBasedRegistry;
import com.envyful.api.registry.impl.LazyRegistry;
import com.envyful.api.registry.impl.StandardMapBasedRegistry;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * An interface representing a registry of keys to values
 *
 * @param <A> The key type
 * @param <B> The value type
 */
public interface Registry<A, B> {

    /**
     *
     * Gets the key serializer
     *
     * @return The key serializer
     */
    KeySerializer<A> keySerializer();

    /**
     *
     * Gets the value for the given key
     *
     * @param key The key
     * @return The value, or null if not found
     */
    @Nullable
    B get(A key);

    /**
     *
     * Gets the value for the given key
     *
     * @param key The key
     * @return The value, or empty if not found
     */
    default Optional<B> getValue(A key) {
        return Optional.ofNullable(this.get(key));
    }

    /**
     *
     * Gets the key for the given value
     *
     * @param value The value
     * @return The key, or null if not found
     */
    A getKey(B value);

    /**
     *
     * Gets the key for the given value
     *
     * @param value The value
     * @return The key, or empty if not found
     */
    default Optional<A> getKeyForValue(B value) {
        return Optional.ofNullable(this.getKey(value));
    }

    /**
     *
     * Registers the given key to the given value
     *
     * @param key The key
     * @param value The value
     */
    void register(A key, B value);

    /**
     *
     * Unregisters the given key
     *
     * @param key The key
     */
    void unregister(A key);

    /**
     *
     * Clears all the values
     *
     */
    void clear();

    /**
     *
     * Gets all the values
     *
     * @return The values
     */
    List<B> values();

    /**
     *
     * Gets all the keys
     *
     * @return The keys
     */
    Set<A> keys();

    /**
     *
     * Gets the type serializer for the values
     *
     * @return The type serializer
     */
    <D> TypeSerializer<D> getTypeSerializer();

    /**
     *
     * Creates a new registry
     *
     * @param <A> The key type
     * @param <B> The value type
     * @return The new registry
     */
    static <A, B> Registry<A, B> create(KeySerializer<A> keySerializer) {
        return new StandardMapBasedRegistry<>(keySerializer, HashMap::new);
    }

    /**
     *
     * Creates a new registry
     *
     * @param <A> The key type
     * @param <B> The value type
     * @return The new registry
     */
    static <A, B> Registry<A, B> concurrent(KeySerializer<A> keySerializer) {
        return new StandardMapBasedRegistry<>(keySerializer, ConcurrentHashMap::new);
    }

    /**
     *
     * Creates a new registry that has a class value
     *
     * @param <A> The key type
     * @param <B> The value type
     * @return The new registry
     */
    static <A, B, C extends Class<B>> Registry<A, C> classBased(KeySerializer<A> keySerializer) {
        return new ClassValueMapBasedRegistry<>(keySerializer, HashMap::new);
    }

    /**
     *
     * Creates a new registry that has a class value
     *
     * @param <A> The key type
     * @param <B> The value type
     * @return The new registry
     */
    static <A, B, C extends Class<B>> Registry<A, C> concurrentClassBased(KeySerializer<A> keySerializer) {
        return new ClassValueMapBasedRegistry<>(keySerializer, ConcurrentHashMap::new);
    }

    /**
     *
     * Creates a new lazy registry that scans for classes with the given annotation,
     * then validates that each discovered class implements/extends the given base type.
     *
     * @param baseType The expected base type of the registered classes
     * @param idAnnotation The annotation containing the registry ID in its value field
     * @return The new lazy registry
     */
    @SuppressWarnings("unchecked")
    static <B, C extends Class<B>> Registry<String, C> scannedClassRegistry(
            Class<? extends B> baseType,
            Class<? extends Annotation> idAnnotation
    ) {
        return new LazyRegistry<>(() -> {
            var registry = new ClassValueMapBasedRegistry<String, B, C>(
                    KeySerializer.identity(),
                    HashMap::new
            );

            var classes = PlatformProxy.scanning().query()
                    .requireAnnotation(idAnnotation)
                    .scan();

            for (var scannedClass : classes) {
                var annotation = scannedClass.annotation(idAnnotation);

                if (annotation == null) {
                    UtilLogger.getLogger().error(
                            "Failed to register class {} in scanned registry, missing annotation {}",
                            scannedClass.type().className(),
                            idAnnotation.getName()
                    );
                    continue;
                }

                var id = annotation.string("value");

                if (id == null || id.isBlank()) {
                    UtilLogger.getLogger().error(
                            "Failed to register class {} in scanned registry, annotation {} does not have a valid value field",
                            scannedClass.type().className(),
                            idAnnotation.getName()
                    );
                    continue;
                }

                try {
                    registry.register(
                            id,
                            (C) scannedClass.loadAs(
                                    baseType,
                                    Thread.currentThread().getContextClassLoader()
                            )
                    );
                } catch (ClassNotFoundException e) {
                    UtilLogger.getLogger().error(
                            "Failed to register class {} in scanned registry, class not found",
                            scannedClass.type().className(),
                            e
                    );
                } catch (IllegalArgumentException e) {
                    UtilLogger.getLogger().error(
                            "Failed to register class {} in scanned registry, class does not implement/extend {}",
                            scannedClass.type().className(),
                            baseType.getName(),
                            e
                    );
                }
            }

            return registry;
        });
    }
}
