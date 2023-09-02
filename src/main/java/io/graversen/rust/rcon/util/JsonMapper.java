package io.graversen.rust.rcon.util;

import lombok.NonNull;

public interface JsonMapper {
    String toJson(@NonNull Object object);

    <T> T fromJson(@NonNull String json, @NonNull Class<T> toClass);
}
