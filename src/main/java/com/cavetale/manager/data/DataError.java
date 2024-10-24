package com.cavetale.manager.data;

import org.jetbrains.annotations.NotNull;

/**
 * Data error, used for severe issues with data handling
 */
public abstract class DataError extends Error {
    public DataError(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
