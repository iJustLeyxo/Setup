package com.cavetale.setup.util.console;

import org.jetbrains.annotations.NotNull;

/**
 * Console output styles, used to manage color, weight, etc. of console outputs
 */
public enum Style {
    PROMPT(Code.BOLD, Code.LIGHT_BLUE_FG),
    INPUT(Code.BOLD, Code.WHITE_FG),
    DEBUG(Code.DARK_GRAY_FG),
    SELECT(Code.LIGHT_BLUE_FG),
    COMMAND(Code.LIGHT_CYAN_FG),
    FLAG(Code.DARK_YELLOW_FG),
    INSTALL(Code.LIGHT_GREEN_FG),
    UPDATE(Code.LIGHT_GREEN_FG),
    UNINSTALL(Code.LIGHT_RED_FG),
    LINK(Code.LIGHT_MAGENTA_FG),
    SUPERFLUOUS(Code.LIGHT_YELLOW_FG),
    MISSING(Code.LIGHT_RED_FG),
    UNKNOWN(Code.DARK_GRAY_FG),
    PLUGIN(Code.LIGHT_BLUE_FG),
    CATEGORY(Code.LIGHT_GREEN_FG),
    SERVER(Code.LIGHT_MAGENTA_FG),
    SOFTWARE(Code.LIGHT_YELLOW_FG),
    INFO(Code.WHITE_FG),
    DONE(Code.LIGHT_GREEN_FG),
    HELP(Code.LIGHT_GREEN_FG),
    WARN(Code.BOLD, Code.LIGHT_YELLOW_FG),
    ERR(Code.BOLD, Code.LIGHT_RED_FG);

    public final @NotNull String xCodes;

    Style(@NotNull Code @NotNull ... codes) {
        StringBuilder s = new StringBuilder();
        for (Code c : codes) {
            s.append(c);
        }
        this.xCodes = s.toString();
    }

    @Override
    public @NotNull String toString() { return this.xCodes; }
}
