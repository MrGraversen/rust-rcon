package io.graversen.rust.rcon.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;

import java.util.List;

public class DefaultJsonMapper implements JsonMapper {
    private ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public String toJson(@NonNull Object object) {
        return getObjectMapper().writeValueAsString(object);
    }

    @Override
    @SneakyThrows
    public <T> T fromJson(@NonNull String json, @NonNull Class<T> toClass) {
        return getObjectMapper().readValue(json, toClass);
    }

    @Override
    @SneakyThrows
    public <T> List<T> fromJsonArray(@NonNull String json, @NonNull Class<T> toClass) {
        final T[] jsonArray = (T[]) getObjectMapper().readValue(json, toClass.arrayType());
        return List.of(jsonArray);
    }

    @Synchronized
    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        return objectMapper;
    }
}
