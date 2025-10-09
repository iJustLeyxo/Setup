package com.cavetale.setup.download;

import org.jetbrains.annotations.NotNull;

public record Ver(@NotNull String ver) {
    public static final @NotNull Ver DEFAULT = Ver.of("0.1-SNAPSHOT");

    @Override
    public String toString() {
        return this.ver;
    }

    public static @NotNull Ver of(@NotNull String ver) {
        return new Ver(ver);
    }
}
