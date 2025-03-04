package com.cavetale.manager.util.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Console manager, used to format console outputs
 */
public final class Console {
    public static @NotNull Detail detail = Detail.STD;
    private static @Nullable Type type = null;
    private static boolean empty = true;

    /**
     * Logs a message to console
     * @param type Type of message to log
     * @param style Style to override the default style of the specified type
     * @param msg Message to log
     * @return {@code true} if the message was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull Style style, @NotNull String msg) {
        if (!Console.logs(type.detail)) return false;
        Console.sep(type);
        System.out.print(Code.RESET + style.toString() + msg);
        Console.empty = false;
        return true;
    }

    /**
     * Logs a type to console
     * @param type Type and style to log
     * @return {@code true} if the style was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type) {
        return Console.log(type, type.style, "");
    }

    /**
     * Logs a style to console
     * @param type Type of style to log
     * @param style Style to override the default style of the specified type
     * @return {@code true} if the style was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull Style style) {
        return Console.log(type, style, "");
    }

    /**
     * Logs a message to console
     * @param type Type of message to log using the default style of the type
     * @return {@code true} if the style was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull String msg) {
        return Console.log(type, type.style, msg);
    }

    /**
     * Logs an exception to console
     * @param type Type of message to log
     * @param t Exception to log
     * @return {@code true} if the exception was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull Throwable t) {
        if (!Console.logs(type)) return false;
        Console.sep();
        Console.log(type);
        Console.logR(t);
        Console.sep();
        return true;
    }

    /**
     * Attempts to log msg with main style under secondary visibility. When failed, logs under main visibility and with extra msg.
     * @param sec Secondary type
     * @param main Main type
     * @param extra Extra message
     * @param msg Main message
     * @return {@code true} in case something was logged
     */
    public static boolean log(@NotNull Type sec, @NotNull Type main, @NotNull String extra, @NotNull String msg) {
        if (!Console.log(sec, main.style, msg)) {
            return Console.log(main, extra + msg);
        }
        return true;
    }

    /**
     * Logs a formatted message to console
     * @param type Type of message to log
     * @param style Style to override the default style of the specified type
     * @param format Format to use for the message
     * @param params Params to format
     * @return {@code true} if the message was logged and not held back due to verbosity
     */
    public static boolean logF(@NotNull Type type, @NotNull Style style, @NotNull String format, @NotNull String... params) {
        if (!Console.logs(type.detail)) return false;
        Console.sep(type);
        System.out.printf(Code.RESET + style.toString() + format, (Object[]) params);
        Console.empty = false;
        return true;
    }

    /**
     * Logs a list to console
     * @param type Type of message to log
     * @param style Style to override the default style of the specified type
     * @param header List header
     * @param cols Columns of the list
     * @param colSize Size of the columns of the list
     * @return {@code true} if the message was logged and not held back due to verbosity
     */
    public static boolean logL(@NotNull Type type, @NotNull Style style, @NotNull String header,
                               int cols, int colSize, @NotNull Object... objects) {
        if (!Console.logs(type.detail)) return false;
        int dashes = (cols * colSize + cols - 1) - header.length() - 2;
        StringBuilder b = new StringBuilder(Code.BOLD + "-".repeat(dashes / 2) + " " + header + " "
                + "-".repeat(dashes / 2 + dashes % 2) + "\n" + Code.WEIGHT_OFF);
        Arrays.sort(objects);
        int i = 1;
        for (Object o : objects) {
            b.append(o).append(" ".repeat(Math.max(0, colSize - o.toString().length())));
            if (i >= cols) {
                b.append("\n");
                i = 1;
            } else {
                b.append(" ");
                i++;
            }
        }
        String s = b.toString();
        if (!s.endsWith("\n")) s += "\n";
        Console.log(type, style, s);
        return true;
    }

    private static void logR(@NotNull Throwable t) {
        if (t instanceof Error) Console.log(Type.ERR);
        else Console.log(Type.WARN);
        System.out.println(Code.BOLD + t.getClass().getSimpleName() + ": " + Code.WEIGHT_OFF + t.getMessage());
        for (StackTraceElement e : t.getStackTrace()) {
            StringBuilder b = new StringBuilder("\t");
            String moduleName = e.getModuleName();
            if (moduleName != null) b.append(moduleName);
            String moduleVersion = e.getModuleVersion();
            if (moduleVersion != null) b.append(" <" + moduleVersion + ">");
            if (moduleName != null || moduleVersion != null) b.append(" /");
            b.append(e.getFileName()).append(": ").append(e.getClassName()).append(".").append(e.getMethodName()).append("() #").append(e.getLineNumber());
            System.out.println(b);
        }
        if (t.getCause() != null) {
            System.out.print("Caused by ");
            Console.logR(t.getCause());
        }
    }

    /**
     * @param detail The detail to test for logging
     * @return {@code true} if the detail level gets logged and not held back due to verbosity
     */
    public static boolean logs(@NotNull Detail detail) {
        return Console.detail.val >= detail.val;
    }

    public static boolean logs(@NotNull Type type) {
        return Console.logs(type.detail);
    }

    /**
     * Test for separation between different types of logged output
     * @param type The type to test for separation, {@code type = null} will always separate
     */
    public static void sep(@Nullable Type type) {
        if (Console.type != type || null == type) {
            if (!Console.empty) System.out.println();
            Console.type = type;
            Console.empty = true;
        }
    }

    /**
     * Force separation between logged outputs
     */
    public static void sep() {
        Console.sep(null);
    }

    /**
     * Get new input arguments from console
     * @return user input argument
     */
    public static @NotNull String in() {
        Console.sep();
        Console.log(Type.PROMPT, "> ");
        Console.log(Type.PROMPT, Style.INPUT);
        return System.console().readLine();
    }

    /**
     * Get an input from console
     * @param prompt Prompt to output
     * @return user's response to the prompt
     */
    public static @NotNull String in(@NotNull String prompt) {
        Console.sep();
        Console.log(Type.PROMPT, prompt + " ");
        Console.log(Type.PROMPT, Style.INPUT);
        return System.console().readLine();
    }

    public static boolean confirm(@NotNull String string) {
        return Console.in(string + " (Y/n)?").equalsIgnoreCase("y");
    }
}
