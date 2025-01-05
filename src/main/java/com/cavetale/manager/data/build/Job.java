package com.cavetale.manager.data.build;

import org.jetbrains.annotations.NotNull;

public record Job(@NotNull String job) {
    @Override
    public String toString() {
        return this.job;
    }

    public static @NotNull Job of(@NotNull String job) {
        return new Job(job);
    }
}
