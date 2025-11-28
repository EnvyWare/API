package com.envyful.api.type.map;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 *
 * The key for the {@link KeyedMap}
 *
 * @param <T> The type of the value
 */
public class Key<T> {

    private final String key;
    private final Class<T> valueType;
    private final BiFunction<String, T, String> placeholder;

    public Key(String key, Class<T> valueType) {
        this.key = key;
        this.valueType = valueType;
        this.placeholder =  (s, t) -> s;
    }

    public Key(String key, Class<T> valueType, BiFunction<String, T, String> placeholder) {
        this.key = key;
        this.valueType = valueType;
        this.placeholder = placeholder;
    }

    public String getKey() {
        return this.key;
    }

    public Class<T> getValueType() {
        return this.valueType;
    }

    public String replace(String s, T value) {
        return this.placeholder.apply(s, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key1 = (Key<?>) o;
        return Objects.equals(key, key1.key) && Objects.equals(valueType, key1.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, valueType);
    }
}
