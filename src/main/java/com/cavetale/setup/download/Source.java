package com.cavetale.setup.download;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/** Download source class. */
public abstract class Source {
    /** The download uri. */
    private final @NotNull URI uri;

    /** The download version. */
    private final @NotNull Ver ver;

    /**
     * Creates a new download source.
     * @param uri The uri to download from.
     * @param ver The download version.
     */
    public Source(@NotNull URI uri, @NotNull Ver ver) {
        this.uri = uri;
        this.ver = ver;
    }

    /**
     * Gets the download version.
     * @return The download version.
     */
    public @NotNull Ver ver() {
        return this.ver;
    }

    /**
     * Gets the download uri.
     * @return The uri to download from.
     */
    public @NotNull URI uri() {
        return this.uri;
    }

    /** Generic download source. */
    public static final class Other extends Source {
        /**
         * Creates a new generic download source.
         * @param uri The uri to download from.
         * @param ver The download version.
         */
        public Other(@NotNull URI uri, @NotNull Ver ver) {
            super(uri, ver);
        }
    }
}
