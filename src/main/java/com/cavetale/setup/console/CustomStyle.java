package com.cavetale.setup.console;

import link.l_pf.cmdlib.io.AbstractStyle;
import link.l_pf.cmdlib.io.Code;
import org.jetbrains.annotations.NotNull;

/** Custom console output styles. */
public enum CustomStyle implements AbstractStyle {
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
    SOFTWARE(Code.LIGHT_YELLOW_FG);

    /** Escape code string */
    public final @NotNull String code;

    /**
     * Creates a new console output style.
     * @param codes The escape codes.
     */
    CustomStyle(@NotNull Code @NotNull ... codes) {
        StringBuilder s = new StringBuilder();
        for (Code c : codes) {
            s.append(c);
        }
        this.code = s.toString();
    }

    @Override
    public @NotNull String toString() { return this.code; }
}
