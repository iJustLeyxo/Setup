package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;

/** Data exception, thrown for minor issues with data handling. */
public abstract class DataException extends Exception {
    /**
     * Creates a new data exception.
     * @param message The exception message.
     */
    public DataException(@NotNull String message) {
        super(message);
    }
}