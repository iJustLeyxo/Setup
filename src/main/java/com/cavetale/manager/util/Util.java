package com.cavetale.manager.util;

import com.cavetale.manager.data.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Download manager, used to download files from the internet
 */
public final class Util {
    public static @NotNull URI uriOf(@NotNull String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new Plugin.URIError(uri, e);
        }
    }

    /**
     * Download a file from the internet
     * @param uri The uri to download from
     * @param target The target file for the download
     * @throws IOException If the download failed
     */
    public static void download(@NotNull URI uri, @NotNull File target) throws IOException {
        target.getParentFile().mkdirs();
        if (target.exists()) throw new RuntimeException(target.getPath() + " already exists.");
        ReadableByteChannel byteChannel = Channels.newChannel(uri.toURL().openStream());
        try (FileOutputStream outputStream = new FileOutputStream(target)) {
            outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        }
    }

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
