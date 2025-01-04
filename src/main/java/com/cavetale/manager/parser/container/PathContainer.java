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
    public boolean option(@NotNull String option) {
        if (this.contents == null) {
            this.contents = option;
            return true;
        }
        return false;
    }
}
