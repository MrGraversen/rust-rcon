package io.graversen.rust.rcon.util;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;

import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Lazy<T> implements Supplier<T> {
    private final @NonNull Supplier<T> supplier;
    private T value;

    public static <T> Lazy<T> of(@NonNull Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @Override
    @Synchronized
    public T get() {
        if (value == null) {
            value = supplier.get();
        }

        return value;
    }
}
