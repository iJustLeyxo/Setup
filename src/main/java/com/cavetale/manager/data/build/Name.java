package com.cavetale.manager.data.build;

import org.jetbrains.annotations.NotNull;

public record Name(@NotNull String name) {
    @Override
    public String toString() {
        return this.name;
    }

    public static @NotNull Name of(@NotNull String name) {
        return new Name(name);
    }
}
