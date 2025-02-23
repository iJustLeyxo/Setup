package com.cavetale.manager.util.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * ANSI escape codes, used to format text outputted to most modern terminal emulators
 */
public enum Code {
    //= Fe Escape Sequences ==
    SINGLE_SHIFT_TWO("N"),
    SINGLE_SHIFT_THREE("O"),
    DEVICE_CONTROL("P"),
    CONTROL_SEQUENCE("["),
    STRING_TERMINATOR("\\"),
    OPERATING_SYSTEM_COMMAND("]"),
    STRING_START("X"),
    PRIVACY_MESSAGE("^"),
    APPLICATION_PROGRAM_COMMAND("_"),

    //= Cursor Movement ==
    UP_1("[A"),
    DOWN_1("[B"),
    FWD_1("[C"),
    BACK_1("[D"),
    NXT_LINE("[E"),
    PREV_LINE("[F"),
    COLUMN_1("[G"),
    CELL_1("[H"),
    SCROLL_UP("[S"),
    SCROLL_DOWN("[T"),
    POSITION_1("[f"),

    //= Erasing ==
    ERASE_AFTER("[0J"),
    ERASE_BEFORE("[1J"),
    ERASE_SCREEN("[2J"),
    ERASE_ALL("[3J"),
    ERASE_LINE_AFTER("[0K"),
    ERASE_LINE_BEFORE("[1K"),
    ERASE_LINE("[2K"),

    //= Cursor ==
    SAVE_CURSOR("[s"),
    RESTORE_CURSOR("[u"),
    SHOW_CURSOR("[25h"),
    HIDE_CURSOR("[25l"),

    //= Utility ==
    FOCUS("[I"),
    UNFOCUS("[O"),
    REPORT_FOCUS_ON("[1049h"),
    REPORT_FOCUS_OFF("[1049l"),
    AUX_ON("[5i"),
    AUX_OFF("[4i"),
    STATUS("[6n"),
    BRACKET_MODE_ON("[2004h"),
    BRACKET_MODE_OFF("[2004l"),

    //= Graphics ==
    RESET("[0m"),
    BOLD("[1m"),
    LIGHT("[2m"),
    ITALIC("[3m"),
    UNDERLINE("[4m"),
    BLINK_SLOW("[5m"),
    BLINK_FAST("[6m"),
    INVERT("[7m"),
    HIDE("[8m"),
    STRIKE_THROUGH("[9m"),
    FONT_PRIMARY("[10m"),
    FONT_GOTHIC("[20m"),
    UNDERLINE_DOUBLE("[21m"),
    WEIGHT_OFF("[22m"),
    ITALIC_OFF("[23m"),
    UNDERLINE_OFF("[24m"),
    BLINK_OFF("[25m"),
    PROPORTIONAL_SPACING_ON("[26m"),
    INVERT_OFF("[27m"),
    HIDE_OFF("[28m"),
    STRIKE_THROUGH_OFF("[29m"),
    PROPORTIONAL_SPACING_OFF("[50m"),
    FRAMED("[51m"),
    CIRCLED("[52m"),
    OVERLINE("[53m"),
    BORDER_OFF("[54m"),
    OVERLINE_OFF("[55m"),
    UNDERLINE_COLOR("[58m"),
    DEFAULT_UNDERLINE_COLOR("[59m"),
    IDEOGRAM_UNDERLINE("[60m"),
    IDEOGRAM_DOUBLE_UNDERLINE("[61m"),
    IDEOGRAM_OVERLINE("[62m"),
    IDEOGRAM_DOUBLE_OVERLINE("[63m"),
    IDEOGRAM_STRESS_MARKING("[64m"),
    IDEOGRAM_OFF("[65m"),
    SUPERSCRIPT("[73m"),
    SUBSCRIPT("[74m"),
    SCRIPT_OFF("[75m"),

    //= Colors ==
    BLACK_FG("[30m"),
    BLACk_BG("[40m"),
    DARK_RED_FG("[31m"),
    DARK_RED_BG("[41m"),
    DARK_GREEN_FG("[32m"),
    DARK_GREEN_BG("[42m"),
    DARK_YELLOW_FG("[33m"),
    DARK_YELLOW_BG("[43m"),
    DARK_BLUE_FG("[34m"),
    DARK_BLUE_BG("[44m"),
    DARK_MAGENTA_FG("[35m"),
    DARK_MAGENTA_BG("[45m"),
    DARK_CYAN_FG("[36m"),
    DARK_CYAN_BG("[46m"),
    LIGHT_GRAY_FG("[37m"),
    LIGHT_GRAY_BG("[47m"),
    DARK_GRAY_FG("[90m"),
    DARK_GRAY_BG("[100m"),
    LIGHT_RED_FG("[91m"),
    LIGHT_RED_BG("[101m"),
    LIGHT_GREEN_FG("[92m"),
    LIGHT_GREEN_BG("[102m"),
    LIGHT_YELLOW_FG("[93m"),
    LIGHT_YELLOW_BG("[103m"),
    LIGHT_BLUE_FG("[94m"),
    LIGHT_BLUE_BG("[104m"),
    LIGHT_MAGENTA_FG("[95m"),
    LIGHT_MAGENTA_BG("[105m"),
    LIGHT_CYAN_FG("[96m"),
    LIGHT_CYAN_BG("[106m"),
    WHITE_FG("[97m"),
    WHITE_BG("[107m"),

    COLOR_IMPLEMENTATION_DEFINED_FG("[38;0m"),
    COLOR_IMPLEMENTATION_DEFINED_BG("[48;0m"),
    TRANSPARENT_FG("[38;1m"),
    TRANSPARENT_BG("[48;1m");

    private final @NotNull String code;

    Code(@NotNull String arg) {
        this.code = Code.of(arg);
    }

    @Override
    public @NotNull String toString() {
        return this.code;
    }

    public static @NotNull String of(@NotNull String arg) {
        return "\u001B" + arg;
    }

    public static @NotNull String up(int rows) {
        return Code.of("[" + rows + "A");
    }

    public static @NotNull String down(int rows) {
        return Code.of("[" + rows + "B");
    }

    public static @NotNull String fwd(int cols) {
        return Code.of("[" + cols + "C");
    }

    public static @NotNull String back(int cols) {
        return Code.of("[" + cols + "D");
    }

    public static @NotNull String nxtLine(int lines) {
        return Code.of("[" + lines + "E");
    }

    public static @NotNull String prevLine(int lines) {
        return Code.of("[" + lines + "F");
    }

    public static @NotNull String column(int col) {
        return Code.of("[" + col + "G");
    }

    public static @NotNull String cell(int row, int col) {
        return Code.of("[" + row + ";" + col + "H");
    }

    public static @NotNull String scrollUp(int lines) {
        return Code.of("[" + lines + "S");
    }

    public static @NotNull String scrollDown(int lines) {
        return Code.of("[" + lines + "T");
    }

    public static @NotNull String position(int row, int col) {
        return Code.of("[" + row + ";" + col + "f");
    }

    public static @NotNull String graphic(@NotNull String arg) {
        return Code.of("[" + arg + "m");
    }

    public static @NotNull String font(@Range(from=0, to=10) byte font) {
        return Code.of("[" + (10 + font) + "m");
    }

    public static @NotNull String darkFGColor(@Range(from=0, to=7) byte color) {
        return Code.of("[" + (30 + color) + "m");
    }

    public static @NotNull String darkBGColor(@Range(from=0, to=7) byte color) {
        return Code.of("[" + (40 + color) + "m");
    }

    public static @NotNull String brightFGColor(@Range(from=0, to=7) byte color) {
        return Code.of("[" + (90 + color) + "m");
    }

    public static @NotNull String brightBGColor(@Range(from=0, to=7) byte color) {
        return Code.of("[" + (100 + color) + "m");
    }

    public static @NotNull String fgColor(@Range(from=0, to=256) short color) {
        return Code.of("[38;5;" + color + "m");
    }

    public static @NotNull String bgColor(@Range(from=0, to=256) short color) {
        return Code.of("[48;5;" + color + "m");
    }

    public static @NotNull String fgColorODA(@Range(from=0, to=256) short color) {
        return Code.of("[38:5:" + color + "m");
    }

    public static @NotNull String bgColorODA(@Range(from=0, to=256) short color) {
        return Code.of("[48:5:" + color + "m");
    }

    public static @NotNull String fgColorRGB(@Range(from=0, to=256) short r, @Range(from=0, to=256) short g, @Range(from=0, to=256) short b) {
        return Code.of("[38;2;" + r + ";" + g + ";" + b + "m");
    }

    public static @NotNull String bgColorRGB(@Range(from=0, to=256) short r, @Range(from=0, to=256) short g, @Range(from=0, to=256) short b) {
        return Code.of("[48;2;" + r + ";" + g + ";" + b + "m");
    }

    public static @NotNull String fgColorCMY(@Range(from=0, to=256) short c, @Range(from=0, to=256) short m, @Range(from=0, to=256) short y) {
        return Code.of("[38;3;" + c + ";" + m + ";" + y + "m");
    }

    public static @NotNull String bgColorCMY(@Range(from=0, to=256) short c, @Range(from=0, to=256) short m, @Range(from=0, to=256) short y) {
        return Code.of("[48;3;" + c + ";" + m + ";" + y + "m");
    }

    public static @NotNull String fgColorCMYK(@Range(from=0, to=256) short c, @Range(from=0, to=256) short m, @Range(from=0, to=256) short y, @Range(from=0, to=256) short k) {
        return Code.of("[38:4::" + c + ":" + m + ":" + y + ":" + k + "::m");
    }

    public static @NotNull String bgColorCMYK(@Range(from=0, to=256) short c, @Range(from=0, to=256) short m, @Range(from=0, to=256) short y, @Range(from=0, to=256) short k) {
        return Code.of("[48:4::" + c + ":" + m + ":" + y + ":" + k + "::m");
    }

    public static @NotNull String fgColor(@NotNull String colorsSpace, @Range(from=0, to=256) short a, @Range(from=0, to=256) short b, @Range(from=0, to=256) short c, @NotNull String unused, @NotNull String csTolerance, @Range(from=0, to=1) byte specificColorSpace) {
        return Code.of("[38;2;" + colorsSpace + ":" + a + ";" + b + ";" + c + ":" + unused + ":" + csTolerance + ":" + specificColorSpace + "m");
    }

    public static @NotNull String bgColor(@NotNull String colorsSpace, @Range(from=0, to=256) short a, @Range(from=0, to=256) short b, @Range(from=0, to=256) short c, @NotNull String unused, @NotNull String csTolerance, @Range(from=0, to=1) byte specificColorSpace) {
        return Code.of("[48;2;" + colorsSpace + ":" + a + ";" + b + ";" + c + ":" + unused + ":" + csTolerance + ":" + specificColorSpace + "m");
    }
}
