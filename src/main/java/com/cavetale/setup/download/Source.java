package com.cavetale.setup.download;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Download source structure.
 * @param link The link to download from.
 * @param version The version the link references.
 */
public record Source (
        @NotNull URI link,
        @NotNull String version
) {
    /**
     * Creates a new download source from a string link.
     * @param link The link to download from.
     * @param version The download version.
     */
    public Source(@NotNull String link, @NotNull String version) {
        this(Link.of(link), version);
    }
}
