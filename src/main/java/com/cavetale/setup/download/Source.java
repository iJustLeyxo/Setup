package com.cavetale.setup.download;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

/** Download source. */
public record Source (
    @NotNull URI link,
    @NotNull String version
) {
    /** Creates a new download source. */
    public static @NotNull Source link(
        @NotNull String link,
        @NotNull String version
    ) {
        return new Source(Link.of(link), version);
    }
}
