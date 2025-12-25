package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;

/** Data error, thrown for severe issues with data handling. */
public abstract class DataError extends Error {
    /**
     * Creates a new data error.
     * @param message The error message.
     * @param cause The error cause.
     */
    public DataError(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
