package io.graversen.v1.rust.rcon.util;

public class TextStyles
{
    private TextStyles()
    {

    }

    public static String bold(String message)
    {
        return String.format("<b>%s</b>", message);
    }

    public static String italic(String message)
    {
        return String.format("<i>%s</i>", message);
    }

    public static String underline(String message)
    {
        return String.format("<u>%s</u>", message);
    }
}
