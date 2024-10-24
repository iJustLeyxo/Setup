package com.cavetale.manager.parser.container;

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
        return this.contents.isEmpty();
    }
}
