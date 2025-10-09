package com.cavetale.setup;

import com.cavetale.setup.data.plugin.Category;
import com.cavetale.setup.data.plugin.Plugin;
import com.cavetale.setup.data.plugin.Server;
import com.cavetale.setup.data.server.Software;
import com.cavetale.setup.parser.Command;
import com.cavetale.setup.parser.Flag;
import com.cavetale.setup.parser.InputException;
import com.cavetale.setup.parser.Parser;
import com.cavetale.setup.util.console.Code;
import com.cavetale.setup.util.console.Console;
import com.cavetale.setup.util.console.Style;
import com.cavetale.setup.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Cavetale installation manager, used to manage plugins and server software for testing servers
 */
public final class Setup {
    public static final @NotNull File TEMP = new File(".setup/");

    public static boolean interactive = true;

    public static void main(String[] args) {
        System.out.println();
        String arg = String.join(" ", args);

        while (interactive) { // Cycle of inputs respective command executions
            if (args != null && args.length > 0) interactive = false;
            else arg = Console.in();
            try {
                Parser parser = new Parser(arg);

                Command cmd = parser.command();
                boolean changed = parser.analyse();

                if (cmd == null) {
                    if (!changed) Console.log(Type.WARN, "Nothing to do. Try typing \"help\".\n");
                } else {
                    Plugin.reset();
                    Category.reset();
                    Server.reset();
                    Software.reset();

                    Console.log(Type.EXTRA, "Running command " + cmd.displayName() + " with args " + parser.args() + " and flags " + parser.flags() + "\n");

                    if (Flag.HELP.isSelected()) {
                        cmd.help(parser);
                        continue;
                    }

                    cmd.run(parser);
                    Console.log(Type.EXTRA, "Finished running command " + cmd.displayName() + "\n");
                }
            } catch (InputException e) {
                Console.log(Type.ERR, e.getMessage() + "\n");
                if (Console.logs(Type.EXTRA)) {
                    Console.log(Type.EXTRA, e);
                }
                if (!interactive) System.exit(1);
            }
            args = null;
        }
        Setup.exit();
    }

    // TODO: Resource pack download (with hash)

    public static void exit() {
        Console.log(Type.EXTRA, "Exiting\n");
        Console.sep();
        System.exit(0);
    }

    public static void help() {
        Console.log(Type.REQUESTED, Style.HELP, Code.BOLD +
                "---------------------------------------- " +
                "Help -----------------------------------------\n" + Code.WEIGHT_OFF +
                "Interactive: java -jar Manager.jar\n" +
                "Single: java -jar Manager.jar <command>\n" +
                "Command: <flag(s)> <command(s)> <flag(s)>\n\n" + Code.BOLD +
                "-------------------------------------- " +
                "Commands ---------------------------------------\n");
        Console.logF(Type.REQUESTED, Style.HELP, "%-16s | %-68s\n", "Command", "Info");
        Console.log(Type.REQUESTED, Style.HELP, "------------------------------------------------" +
                "---------------------------------------\n");
        ArrayList<Command> commands = new ArrayList<>(List.of(Command.values()));
        Collections.sort(commands);
        for (Command c : commands) {
            Console.logF(Type.REQUESTED, Style.HELP, "%-16s | %-68s\n",
                    c.refs[0], c.info);
        }
        Console.log(Type.REQUESTED, Style.HELP, Code.BOLD +
                "\n--------------------------------------- " +
                "Flags -----------------------------------------\n");
        Console.logF(Type.REQUESTED, Style.HELP, "%-16s | %-32s | %-33s\n", "Flag", "Info", "Usage");
        Console.log(Type.REQUESTED, Style.HELP, "------------------------------------------------" +
                "---------------------------------------\n");
        ArrayList<Flag> flags = new ArrayList<>(List.of(Flag.values()));
        Collections.sort(flags);
        for (Flag f : flags) {
            Console.logF(Type.REQUESTED, Style.HELP, "%2s %-13s | %-32s | %-33s\n", "-" + f.ref(),
                    "--" + f.displayName(), f.info(), Objects.requireNonNullElse(f.usage(), ""));
        }
    }
}