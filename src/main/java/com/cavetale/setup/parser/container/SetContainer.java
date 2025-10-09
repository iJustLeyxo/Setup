package com.cavetale.setup.parser.container;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Container of a set of elements
 * @param <T> The type of the elements
 */
public abstract class SetContainer<T> extends Container<Set<T>> {
    public SetContainer() {
        super(new HashSet<>());
    }

    @Override
    public boolean isEmpty() {
        return this.get().isEmpty();
    }

    @Override
    public @NotNull Set<T> get() {
        assert this.contents != null;
        return this.contents;
    }

    @Override
    public void clear() {
        this.get().clear();
    }
}
