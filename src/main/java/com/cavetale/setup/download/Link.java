package com.cavetale.setup.download;

import com.cavetale.setup.data.DataError;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

/** Utility class for handling links, uris and urls. */
public final class Link {
    /**
     * Converts a string into a uri.
     * @param link The link t convert.
     * @return The converted link.
     */
    static @NotNull URI of(@NotNull String link) {
        try {
            return new URI(link);
        } catch (URISyntaxException e) {
            throw new LinkError(link, e);
        }
    }

    /** Exception that is thrown when an error occurs in a uri. */
    public static class LinkError extends DataError {
        /**
         * Creates a new link error.
         * @param link The invalid reference.
         * @param cause The original error.
         */
        public LinkError(@NotNull String link, @NotNull Throwable cause) {
            super("Faulty link\n" + link + ": " + cause.getMessage(), cause);
        }
    }
}
