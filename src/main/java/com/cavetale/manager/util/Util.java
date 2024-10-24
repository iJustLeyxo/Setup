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
    public static void download(URI uri, File target) throws IOException {
        if (target.exists()) throw new RuntimeException(target.getPath() + " already exists.");
        ReadableByteChannel byteChannel = Channels.newChannel(uri.toURL().openStream());
        try (FileOutputStream outputStream = new FileOutputStream(target)) {
            outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        }
    }
}
