package io.graversen.v1.rust.rcon.util;

import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class Utils
{
    public static final Pattern squareBracketInsideMatcher = Pattern.compile("\\[(.*?)\\]");

    public static final Pattern squareBracketOutsideMatcher = Pattern.compile("\\](.*?)\\[");

    public static String partialJoin(String delimiter, String[] stringArray, int fromIndex, int toIndex)
    {
        final String[] partialStringArray = Arrays.copyOfRange(stringArray, fromIndex, toIndex);
        return String.join(delimiter, partialStringArray);
    }

    public static int nthIndexOf(String text, char needle, int n)
    {
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == needle)
            {
                n--;
                if (n == 0)
                {
                    return i;
                }
            }
        }
        return -1;
    }
}
