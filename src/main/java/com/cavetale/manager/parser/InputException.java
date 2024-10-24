package com.cavetale.manager.parser;

import org.jetbrains.annotations.NotNull;

/**
 * Input exception, used for invalid inputs to return to the main method
 */
public abstract class InputException extends Exception {
    public InputException(@NotNull String message) {
        super(message);
    }
}
