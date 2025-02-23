package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.parser.container.Container;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Detail;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public final class Parser {
    private final @Nullable Command command;
    private final @NotNull List<String> args = new LinkedList<>();
    private final @NotNull List<Flag> flags = new LinkedList<>();

    private final char[] chars;
    private int i;

    /**
     * Parses input arguments
     * @param arg Argument to parse
     * @throws InputException When an invalid input was found
     */
    public Parser(@NotNull String arg) throws InputException {
        Console.log(Type.EXTRA, "Parsing input\n");

        for (Command c : Command.values()) c.reset(); // Reset commands and flags
        for (Flag f : Flag.values()) f.reset();

        chars = arg.toCharArray(); // Parse
        i = 0;

        while(flag()); // Parse flags before command

        skip(); // Parse command

        StringBuilder builder = new StringBuilder();
        for (; i < chars.length; i++) {
            char c = chars[i];
            if (c == ' ' || c == '-') break;
            builder.append(c);
        }

        if (builder.isEmpty()) this.command = null;
        else {
            this.command = Command.get(builder.toString());
            this.command.target();
        }

        this.args.addAll(this.arg()); // Parse command arguments

        while (flag()); // Parse flags after command

        this.flags.clear(); // Reload flags
        for (Flag f : Flag.values()) if (f.isSelected()) this.flags.add(f);

        Console.log(Type.EXTRA, "Parsing done\n");
    }

    private boolean flag() throws InputException {
        skip();

        if (i < 0 || chars.length - 2 < i || chars[i] != '-') return false;

        Flag flag = null;

        if (i <= chars.length - 4 && chars[i + 1] == '-' && chars[i + 2] != ' ' && chars[i + 2] != '-' && chars[i + 3] != ' ' && chars[i + 3] != '-') { // Parse long flag
            StringBuilder builder = new StringBuilder();
            for (i += 2; i < chars.length; i++) {
                char c = chars[i];
                if (c == ' ' || c == '-') break;
                builder.append(c);
            }

            Console.log(Type.DEBUG, "Parsed flag --" + builder + "\n");
            flag = Flag.get(builder.toString());
            flag.target();
        } else if (chars[i + 1] != ' ' && chars[i + 1] != '-') { // Parse short flag(s)
            for (i++; i < chars.length; i++) {
                char c = chars[i];
                if (c == ' ' || c == '-') break;
                flag = Flag.get(c);
                flag.target();
                Console.log(Type.DEBUG, "Parsed flag -" + c + "\n");
            }
        } else {
            return false;
        }

        assert flag != null; // Parse flag arguments
        Container<?> container = flag.container();
        if (container == null) return false;

        for (String s : this.arg()) container.add(s);

        return true;
    }

    private @NotNull List<String> arg() {
        List<String> args = new LinkedList<>();

        StringBuilder builder;
        boolean exit = false;
        while(!exit) {
            skip();
            builder = new StringBuilder();
            for (; i < chars.length; i++) {
                char c = chars[i];
                if (c == ' ') break;
                if (c == '-') {
                    exit = true;
                    break;
                }
                builder.append(c);
            }

            if (!builder.isEmpty()) {
                String string = builder.toString();
                args.add(string);
                Console.log(Type.DEBUG, "Parsed argument " + string + "\n");
            }

            else exit = true;
        }

        return args;
    }

    private void skip() {
        while (i < chars.length && chars[i] == ' ') i++;
    }

    public @Nullable Command command() {
        return this.command;
    }

    public @NotNull List<String> args() {
        return this.args;
    }

    public @NotNull List<Flag> flags() {
        return this.flags;
    }

    /**
     * Analyse the parsed tokens for some basic flags
     * @return {@code true} if something has changes
     */
    public boolean analyse() {
        Console.log(Type.EXTRA, "Analysing flags");
        StringBuilder s = new StringBuilder();
        if (Flag.DEBUG.isSelected()) {
            Console.detail = Detail.MAX;
            s.append("Debug mode activated\n");
        } else if (Flag.VERBOSE.isSelected()) {
            Console.detail = Detail.HIGH;
            s.append("Verbose mode activated\n");
        } else if (Flag.NORMAL.isSelected()) {
            Console.detail = Detail.STD;
            s.append("Default verbosity mode activated\n");
        } else if (Flag.QUIET.isSelected()) {
            Console.detail = Detail.LOW;
            s.append("Quiet mode activated\n");
        }
        if (Flag.INTERACTIVE.isSelected() && !Manager.interactive) {
            Manager.interactive = true;
            s.append("Interactive mode activated\n");
        }
        if (!s.isEmpty()) {
            Console.log(Type.INFO, s.toString());
            Console.log(Type.EXTRA, "Finished analysing tokens\n");
            return true;
        }
        Console.log(Type.EXTRA, Style.DONE, " done\n");
        return false;
    }
}
