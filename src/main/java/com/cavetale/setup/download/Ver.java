package com.cavetale.setup.download;

import org.jetbrains.annotations.NotNull;

/**
 * Download version.
 * @param ver The download version identifier.
 */
public record Ver(@NotNull String ver) {
    /** The most common (default) download version. */
    public static final @NotNull Ver DEFAULT = Ver.of("0.1-SNAPSHOT");

    @Override
    public String toString() {
        return this.ver;
    }

    /**
     * Creates a new download version.
     * @param ver The version identifier.
     * @return The new download version.
     */
    public static @NotNull Ver of(@NotNull String ver) {
        return new Ver(ver);
    }
}
