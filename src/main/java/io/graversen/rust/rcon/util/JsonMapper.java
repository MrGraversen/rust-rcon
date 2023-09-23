package io.graversen.rust.rcon.util;

import lombok.NonNull;

import java.util.List;

public interface JsonMapper {
    String toJson(@NonNull Object object);

    <T> T fromJson(@NonNull String json, @NonNull Class<T> toClass);

    <T> List<T> fromJsonArray(@NonNull String json, @NonNull Class<T> toClass);
}
