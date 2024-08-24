package com.playmonumenta.papermixins.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private Object impl;
    private boolean init = false;

    public Lazy(Supplier<T> supplier) {
        this.impl = supplier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        if (!init) {
            impl = ((Supplier<T>) impl).get();
            init = true;
        }

        return (T) impl;
    }
}
