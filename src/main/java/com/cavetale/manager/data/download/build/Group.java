package com.cavetale.manager.data.download.build;

import org.jetbrains.annotations.NotNull;

public record Group(@NotNull String group) {
    public static @NotNull Group DEFAULT = Group.of("com.cavetale");

    @Override
    public String toString() {
        return this.group;
    }

    public static @NotNull Group of(@NotNull String group) {
        return new Group(group);
    }

    public static @NotNull Group of(@NotNull Parent parent, @NotNull String child) {
        return new Group(parent.group() + "." + child);
    }

    public static @NotNull Group of(@NotNull Parent parent, @NotNull Ref child) {
        return new Group(parent.group() + "." + child);
    }
}
