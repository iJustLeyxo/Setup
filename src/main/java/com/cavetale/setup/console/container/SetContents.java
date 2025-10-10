package com.cavetale.setup.console.container;

import io.github.ijustleyxo.jclix.app.container.Contents;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Container for a set of elements of a generic type.
 * @param <T> The type of contents the container carries.
 */
public abstract class SetContents<T> implements Contents<Set<T>> {
    /** The container contents. */
    protected final @NotNull Set<T> contents = new HashSet<>(16);

    @Override
    public @NotNull Set<T> contents() {
        return this.contents;
    }

    @Override
    public boolean isEmpty() {
        return this.contents.isEmpty();
    }

    @Override
    public void clear() {
        this.contents.clear();
    }
}
