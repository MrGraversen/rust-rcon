package io.graversen.rust.rcon.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class RustUtils {
    private static final DateTimeFormatter RUST_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    public static ZonedDateTime parseRustDateTime(@NonNull String dateTime) {
        final var localDateTime = LocalDateTime.parse(dateTime, RUST_DATE_TIME_FORMATTER);
        return localDateTime.atZone(CommonUtils.utc());
    }
}
