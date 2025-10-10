package com.cavetale.setup.console;

import io.github.ijustleyxo.jclix.io.AbstractStyle;
import io.github.ijustleyxo.jclix.io.Code;
import org.jetbrains.annotations.NotNull;

/**
 * Console output styles, used to manage color, weight, etc. of console outputs
 */
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

    public final @NotNull String xCodes;

    CustomStyle(@NotNull Code @NotNull ... codes) {
        StringBuilder s = new StringBuilder();
        for (Code c : codes) {
            s.append(c);
        }
        this.xCodes = s.toString();
    }

    @Override
    public @NotNull String toString() { return this.xCodes; }
}
