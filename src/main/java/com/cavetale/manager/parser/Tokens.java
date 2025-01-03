package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.parser.container.DummyContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Detail;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Parsing tokens, used to store the result of parsing a list of arguments
 * @param commands Detected commands
 * @param flags Detected flags and flag options / arguments
 */
public record Tokens (@NotNull Set<Command> commands, @NotNull Map<Flag, DummyContainer> flags) {
    /**
     * Analyse the parsed tokens for some basic flags
     * @return {@code true} if something of significance has been found
     */
    public boolean analyse() {
        Console.log(Type.DEBUG, "Analysing tokens");
        StringBuilder s = new StringBuilder();
        if (this.flags().containsKey(Flag.verbose)) {
            Console.detail = Detail.HIGH;
            s.append("Verbose mode activated\n");
        } else if (this.flags().containsKey(Flag.normal)) {
            Console.detail = Detail.STD;
            s.append("Default verbosity mode activated\n");
        } else if (this.flags().containsKey(Flag.quiet)) {
            Console.detail = Detail.LOW;
            s.append("Quiet mode activated\n");
        }
        if (this.flags().containsKey(Flag.interactive) && !Manager.interactive) {
            Manager.interactive = true;
            s.append("Interactive mode activated\n");
        }
        if (!s.isEmpty()) {
            Console.log(Type.INFO, s.toString());
            Console.log(Type.DEBUG, "Finished analysing tokens\n");
            return true;
        }
        Console.log(Type.DEBUG, Style.DONE, " done\n");
        return false;
    }
}
