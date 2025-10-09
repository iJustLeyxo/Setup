package com.cavetale.setup.data.build;

import org.jetbrains.annotations.NotNull;

public record Ref(@NotNull String ref) {
    @Override
    public String toString() {
        return this.ref;
    }

    public @NotNull String lower() {
        return this.ref.toLowerCase();
    }

    public static @NotNull Ref of(@NotNull String ref) {
        return new Ref(ref);
    }
}
