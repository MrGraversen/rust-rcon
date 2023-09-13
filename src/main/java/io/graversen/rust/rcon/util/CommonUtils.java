package io.graversen.rust.rcon.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;

@UtilityClass
public class CommonUtils {
    public static final Pattern SQUARE_BRACKET_INSIDE_MATCHER = Pattern.compile("\\[(.*?)\\]");

    public static final Pattern SQUARE_BRACKET_OUTSIDE_MATCHER = Pattern.compile("\\](.*?)\\[");

    public static ZonedDateTime now() {
        return ZonedDateTime.now(utc());
    }

    public static ZoneId utc() {
        return ZoneId.of("UTC");
    }

    public static String partialJoin(String delimiter, String[] stringArray, int fromIndex, int toIndex) {
        final String[] partialStringArray = Arrays.copyOfRange(stringArray, fromIndex, toIndex);
        return String.join(delimiter, partialStringArray);
    }

    public static int nthIndexOf(String text, char needle, int n) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == needle) {
                n--;
                if (n == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    @SneakyThrows
    public static URI uri(@NonNull String uri) {
        return new URI(uri);
    }

    public static int processors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isBlank();
    }
}
