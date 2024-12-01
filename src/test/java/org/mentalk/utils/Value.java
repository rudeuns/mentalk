package org.mentalk.utils;

public class Value<T> {

    private final T value;
    private final boolean isDefault;

    private Value(T value, boolean isDefault) {
        this.value = value;
        this.isDefault = isDefault;
    }

    public static <T> Value<T> of(T value) {
        return new Value<>(value, false);
    }

    public static <T> Value<T> defaults() {
        return new Value<>(null, true);
    }

    public T orElse(T defaultValue) {
        return isDefault ? defaultValue : value;
    }
}
