package com.cavetale.manager.data.build;

import org.jetbrains.annotations.NotNull;

public record Parent(@NotNull String group) {
    public static final @NotNull Parent DEFAULT = Parent.of("com.cavetale");

    @Override
    public String toString() {
        return this.group;
    }

    public static @NotNull Parent of(@NotNull String group) {
        return new Parent(group);
    }
}
