package io.graversen.v1.rust.rcon.util;

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
        return String.format(COLOR_TEMPLATE, "#ff0000ff", message);
    }

    public static String green(String message)
    {
        return String.format(COLOR_TEMPLATE, "#008000ff", message);
    }

    public static String blue(String message)
    {
        return String.format(COLOR_TEMPLATE, "#0000ffff", message);
    }

    public static String yellow(String message)
    {
        return String.format(COLOR_TEMPLATE, "#ffff00ff", message);
    }

    public static String orange(String message)
    {
        return String.format(COLOR_TEMPLATE, "#ffa500ff", message);
    }

    public static String grey(String message)
    {
        return String.format(COLOR_TEMPLATE, "#808080ff", message);
    }

    public static String chat(String message)
    {
        return String.format(COLOR_TEMPLATE, "#55AAFF", message);
    }
}
