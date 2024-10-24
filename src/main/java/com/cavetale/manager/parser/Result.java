package com.cavetale.manager.parser;

import com.cavetale.manager.data.plugin.PlugIndexer;
import com.cavetale.manager.data.server.SoftwareIndexer;
import org.jetbrains.annotations.NotNull;

/**
 * Parsing result, used to store the result of parsing a list of arguments
 * @param tokens Resulting tokens of parsing
 * @param plugIndexer Plugin manager for the resulting tokens
 * @param softwareIndexer Software manager for the resulting tokens
 */
public record Result (
        @NotNull Tokens tokens,
        @NotNull PlugIndexer plugIndexer,
        @NotNull SoftwareIndexer softwareIndexer) {
}
