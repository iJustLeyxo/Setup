package com.cavetale.manager.parser.container;

import com.cavetale.manager.parser.InputException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Container of an element of a generic type
 * @param <T> Type of the contents
 */
public abstract class Container<T> extends NotAContainer {
    protected @Nullable T contents;

    public Container(@Nullable T contents) {
        this.contents = contents;
    }

    /**
     * Adds an option to the container
     * @param option The option to add
     * @return {@code true} if the option could be added
     * @throws InputException If adding should've been possible but failed
     */
    public abstract boolean option(@NotNull String option) throws InputException;

    public boolean isEmpty() {
        return this.contents == null;
    }

    public @Nullable T get() {
        return this.contents;
    }
}
