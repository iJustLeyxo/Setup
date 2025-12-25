package com.cavetale.setup.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Utility collection. */
public final class Util {
    /**
     * Converts CAPS_WITH_UNDERSCORES to CamelCase
     * @param string The string to convert.
     * @return The converted string.
     */
    public static @NotNull String capsToCamel(@NotNull String string) {
        return Arrays.stream(string.split("_")).map(s -> s.isEmpty() ? "" : s.charAt(0) + s.substring(1).toLowerCase()).collect(Collectors.joining());
    }

    /**
     * Computes the similarity between two strings.
     * @param s1 The first string.
     * @param s2 The second string.
     * @return The similarity between 0 (maximum) and 1 (identical).
     */
    public static double similarity(@NotNull String s1, @NotNull String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }

        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; // Both strings are empty
        }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    /**
     * Utility function for the similarity computation.
     * @param s1 The first string.
     * @param s2 The second string.
     * @return Some magic value.
     */
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }
}
