package com.cavetale.manager.parser;

import com.cavetale.manager.data.plugin.PlugIndexer;
import com.cavetale.manager.data.server.SoftwareIndexer;
import com.cavetale.manager.parser.container.Container;
import com.cavetale.manager.parser.container.NotAContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class Parser {
    /**
     * Parses input arguments
     * @param args Arguments to parse
     * @return A parsing result
     * @throws InputException When an invalid input was found
     */
    public static @NotNull Result parse(String[] args) throws InputException {
        Console.log(Type.DEBUG, "Parsing input\n");
        Set<Command> commands = new LinkedHashSet<>();
        Map<Flag, NotAContainer> flags = new HashMap<>();
        Flag flag = null;
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                flag = null;
                if (arg.length() > 2 && arg.charAt(1) == '-') {
                    flag = Flag.get(arg.substring(2));
                    if (!flags.containsKey(flag)) flags.put(flag, flag.container());
                } else if (arg.length() > 1) {
                    for (int i = 1; i < arg.length(); i++) {
                        flag = Flag.get(arg.charAt(i));
                        if (!flags.containsKey(flag)) flags.put(flag, flag.container());
                    }
                }
            } else {
                if (flag != null && (!(flags.get(flag) instanceof Container<?> container) || !container.option(arg))) flag = null;
                if (flag == null) {
                    Command cmd = Command.get(arg);
                    if (commands.contains(cmd)) Console.log(Type.INFO, "Ignoring duplicate command \"" + arg + "\n");
                    commands.add(cmd);
                }
            }
        }
        Tokens tokens = new Tokens(commands, flags);
        Console.log(Type.DEBUG, "Finished parsing done\n");
        return new Result(tokens, new PlugIndexer(tokens), new SoftwareIndexer(tokens));
    }
}
