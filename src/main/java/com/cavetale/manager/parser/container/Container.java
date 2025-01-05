package com.cavetale.manager.parser.container;

import com.cavetale.manager.parser.InputException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Container of an element of a generic type
 * @param <T> Type of the contents
 */
public abstract class Container<T> {
    protected @Nullable T contents;

    public Container(@Nullable T contents) {
        this.contents = contents;
    }

    public abstract boolean add(@NotNull String arg) throws InputException;

    public boolean isEmpty() {
        return this.contents == null;
    }

    public @Nullable T get() {
        return this.contents;
    }

    public abstract void clear();
}
