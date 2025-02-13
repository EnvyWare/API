package com.envyful.api.type;

import java.util.*;

/**
 *
 * A simple generic implementation of a Map that has a timeout for each entry. If the entry has not been accessed within
 * the timeout period, it will be removed from the map
 * <br>
 * The values are only invalidated when they are accessed, not when they are added
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class TimeOutHashMap<K, V> implements Map<K, V> {

    private final long cacheDuration;

    private final Map<K, Pair<Long, V>> backingMap = new HashMap<>();

    public TimeOutHashMap(long cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    @Override
    public int size() {
        return this.backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Pair<Long, V> longVPair : this.backingMap.values()) {
            if (!this.hasTimedOut(longVPair.getX()) && Objects.equals(value, longVPair.getY())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public V get(Object key) {
        Pair<Long, V> entry = this.backingMap.get(key);

        if (entry == null || this.hasTimedOut(entry.getX())) {
            return null;
        }

        return entry.getY();
    }

    @Override
    public V put(K key, V value) {
        Pair<Long, V> old =  this.backingMap.put(key, Pair.of(System.currentTimeMillis(), value));
        return old == null ? null : old.getY();
    }

    @Override
    public V remove(Object key) {
        Pair<Long, V> old = this.backingMap.remove(key);
        return old == null ? null : old.getY();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.backingMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.backingMap.keySet();
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();

        for (Pair<Long, V> value : this.backingMap.values()) {
            if (!this.hasTimedOut(value.getX())) {
                values.add(value.getY());
            }
        }

        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> values = new HashSet<>();

        for (Entry<K, Pair<Long, V>> value : this.backingMap.entrySet()) {
            if (!this.hasTimedOut(value.getValue().getX())) {
                values.add(Pair.of(value.getKey(), value.getValue().getValue()));
            }
        }

        return values;
    }

    private boolean hasTimedOut(long time) {
        return (System.currentTimeMillis() - time) >= this.cacheDuration;
    }
}
