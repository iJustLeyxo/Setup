package com.cavetale.setup.cmd;

import link.l_pf.cmdlib.shell.Code;
import link.l_pf.cmdlib.shell.Style;
import org.jetbrains.annotations.NotNull;

import static link.l_pf.cmdlib.shell.Code.Std.*;

/** Custom styles for the command line app. */
public enum CustomStyle implements Style {
    SELECT(LIGHT_BLUE_FG),
    COMMAND(LIGHT_CYAN_FG),
    FLAG(DARK_YELLOW_FG),
    INSTALLED(LIGHT_GREEN_FG),
    UPDATE(LIGHT_GREEN_FG),
    UNINSTALL(LIGHT_RED_FG),
    LINK(LIGHT_MAGENTA_FG),
    SUPERFLUOUS(LIGHT_YELLOW_FG),
    MISSING(LIGHT_RED_FG),
    UNKNOWN(DARK_GRAY_FG),
    PLUGIN(LIGHT_BLUE_FG),
    CATEGORY(LIGHT_GREEN_FG),
    SERVER(LIGHT_MAGENTA_FG),
    SOFTWARE(LIGHT_YELLOW_FG);

    // As per library specifications...

    public final @NotNull String code;

    CustomStyle(@NotNull Code @NotNull ... codes) {
        StringBuilder s = new StringBuilder();
        for (Code c : codes) s.append(c);
        this.code = s.toString();
    }

    @Override
    public @NotNull String toString() { return this.code; }
}
