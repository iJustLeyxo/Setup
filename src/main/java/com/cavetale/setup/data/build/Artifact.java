package com.cavetale.setup.data.build;

import org.jetbrains.annotations.NotNull;

public record Artifact(@NotNull String artifact) {
    @Override
    public String toString() {
        return this.artifact;
    }

    public static @NotNull Artifact of(@NotNull String artifact) {
        return new Artifact(artifact);
    }

    public static @NotNull Artifact of(@NotNull Ref ref) {
        return new Artifact(ref.toString());
    }
}
