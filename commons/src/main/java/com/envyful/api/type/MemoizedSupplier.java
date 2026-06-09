package com.envyful.api.type;

import java.util.function.Supplier;

public class MemoizedSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value;

    public MemoizedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.value == null) {
            this.value = supplier.get();
        }

        return value;
    }
}
