package com.cavetale.manager.util.console;

import org.jetbrains.annotations.NotNull;

public enum Type {
    PROMPT(Detail.OVERRIDE, Style.PROMPT),
    REQUESTED(Detail.OVERRIDE, Style.INFO),
    HELP(Detail.OVERRIDE, Style.HELP),
    DEBUG(Detail.HIGH, Style.DEBUG),
    INFO(Detail.STD, Style.INFO),
    WARN(Detail.LOW, Style.WARN),
    ERR(Detail.MIN, Style.ERR);

    public final @NotNull Detail detail;
    public final @NotNull Style style;

    Type(@NotNull Detail detail, @NotNull Style style) {
        this.detail = detail;
        this.style = style;
    }
}