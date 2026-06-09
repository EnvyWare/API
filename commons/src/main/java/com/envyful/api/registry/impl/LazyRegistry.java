package com.envyful.api.registry.impl;

import com.envyful.api.registry.Registry;
import com.envyful.api.registry.config.KeySerializer;
import com.envyful.api.type.MemoizedSupplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class LazyRegistry<A, B> implements Registry<A, B> {

    private final Supplier<Registry<A, B>> supplier;

    public LazyRegistry(Supplier<Registry<A, B>> supplier) {
        this.supplier = new MemoizedSupplier<>(supplier);
    }

    @Override
    public KeySerializer<A> keySerializer() {
        return this.supplier.get().keySerializer();
    }

    @Override
    public @Nullable B get(A key) {
        return this.supplier.get().get(key);
    }

    @Override
    public Optional<B> getValue(A key) {
        return this.supplier.get().getValue(key);
    }

    @Override
    public A getKey(B value) {
        return this.supplier.get().getKey(value);
    }

    @Override
    public Optional<A> getKeyForValue(B value) {
        return this.supplier.get().getKeyForValue(value);
    }

    @Override
    public void register(A key, B value) {
        this.supplier.get().register(key, value);
    }

    @Override
    public void unregister(A key) {
        this.supplier.get().unregister(key);
    }

    @Override
    public void clear() {
        this.supplier.get().clear();
    }

    @Override
    public List<B> values() {
        return this.supplier.get().values();
    }

    @Override
    public Set<A> keys() {
        return this.supplier.get().keys();
    }

    @Override
    public <D> TypeSerializer<D> getTypeSerializer() {
        return this.supplier.get().getTypeSerializer();
    }
}
