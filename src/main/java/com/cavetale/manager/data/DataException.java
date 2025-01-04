package com.cavetale.manager.data;

import org.jetbrains.annotations.NotNull;

/**
 * Data exception, used for minor issues with data handling
 */
public abstract class DataException extends Exception {
    public DataException(@NotNull String message) {
        super(message);
    }
}