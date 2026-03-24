package com.envyful.api.type.map;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.parse.SimplePlaceholder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 * A map that utilises generics, with the key, to cast the value to the
 * desired type and hide the casting from the user
 *
 */
public class KeyedMap implements SimplePlaceholder, Map<Key<?>, Object> {

    private static final KeyedMap EMPTY = new KeyedMap(Map.of());

    private final Map<Key<?>, Object> map;

    public KeyedMap() {
        this(new HashMap<>());
    }

    public KeyedMap(Map<Key<?>, Object> backingMap) {
        this.map = backingMap;
    }

    public <T> void putKey(Key<T> key, T value) {
        this.map.put(key, value);
    }

    @Nullable
    public <T> T getKey(Key<T> key) {
        if (!this.map.containsKey(key)) {
            return null;
        }

        return (T) this.map.get(key);
    }

    public static KeyedMap empty() {
        return EMPTY;
    }

    @Override
    public @NonNull ParseResult replace(@NonNull ParseResult line) {
        return SimplePlaceholder.super.replace(line);
    }

    @Override
    public String replace(String s) {
        for (var entry : this.map.entrySet()) {
            Key key = entry.getKey();
            s = key.replace(s, entry.getValue());
        }
        return s;
    }

    @Override
    public Object put(Key<?> key, Object value) {
        return this.map.put(key, value);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.map.get(key);
    }

    @Override
    public Object remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Key<?>, ?> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<Key<?>> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<Key<?>, Object>> entrySet() {
        return this.map.entrySet();
    }
}
