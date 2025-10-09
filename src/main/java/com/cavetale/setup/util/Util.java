package com.cavetale.setup.util;

import com.cavetale.setup.Setup;
import com.cavetale.setup.data.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Download manager, used to download files from the internet
 */
public final class Util {
    public static @NotNull String capsToCamel(@NotNull String name) {
        return Arrays.stream(name.split("_")).map(s -> s.isEmpty() ? "" : s.charAt(0) + s.substring(1).toLowerCase()).collect(Collectors.joining());
    }

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
     * @param folder The target folder
     * @param name The target file name
     * @throws IOException If the download failed
     */
    public static void download(@NotNull URI uri, @NotNull File folder, @NotNull String name) throws IOException {
        File file = Util.stash(uri);
        finalise(file, folder, name);
    }

    public static @NotNull File stash(@NotNull URI uri) throws IOException {
        Setup.TEMP.mkdirs();
        File file;

        do {
            file = new File(Setup.TEMP, UUID.randomUUID().toString());
        } while (file.exists());

        ReadableByteChannel byteChannel = Channels.newChannel(uri.toURL().openStream());
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);

        return file;
    }

    public static void finalise(@NotNull File stash, @NotNull File folder, @NotNull String name) throws IOException {
        folder.mkdirs();
        Files.copy(stash.toPath(), new File(folder, name).toPath());
        stash.delete();
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
