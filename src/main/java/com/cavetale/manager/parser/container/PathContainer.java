package com.cavetale.manager.parser.container;

import org.jetbrains.annotations.NotNull;

/**
 * Path container, used to store a path from a path flag
 */
public final class PathContainer extends Container<String> {
    public PathContainer() {
        super(null);
    }

    @Override
    public boolean add(@NotNull String arg) {
        if (this.contents == null) {
            this.contents = arg;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.contents = null;
    }
}
