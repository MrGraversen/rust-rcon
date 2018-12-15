package io.graversen.rust.rcon.util;

public class Colors
{
    private static final String COLOR_TEMPLATE = "<color=%s>%s</color>";

    private Colors()
    {

    }

    public static String custom(String color, String message)
    {
        return String.format(COLOR_TEMPLATE, color, message);
    }

    public static String red(String message)
    {
        return String.format(COLOR_TEMPLATE, "red", message);
    }

    public static String green(String message)
    {
        return String.format(COLOR_TEMPLATE, "green", message);
    }

    public static String blue(String message)
    {
        return String.format(COLOR_TEMPLATE, "blue", message);
    }

    public static String yellow(String message)
    {
        return String.format(COLOR_TEMPLATE, "yellow", message);
    }

    public static String orange(String message)
    {
        return String.format(COLOR_TEMPLATE, "orange", message);
    }
}
