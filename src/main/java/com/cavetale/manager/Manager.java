package com.cavetale.manager;

import com.cavetale.manager.data.plugin.*;
import com.cavetale.manager.data.server.Softwares;
import com.cavetale.manager.parser.*;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;

import java.util.*;

/**
 * Cavetale installation manager, used to manage plugins and server software for testing servers
 */
public final class Manager {
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
                    Plugins.reloadInstallations();
                    Categories.reloadInstallations();
                    Servers.reloadInstallations();
                    Softwares.reloadInstallations();
                    Softwares.reloadSelected(parser);
                    Servers.reloadSelected(parser);
                    Categories.reloadSelected(parser);
                    Plugins.reloadSelected(parser);

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
        Manager.exit();
    }

    // TODO: Resource pack download (with hash)

    public static void exit() {
        Console.log(Type.EXTRA, "Exiting\n");
        Console.sep();
        System.exit(0);
    }

    public static void help() {
        Console.log(Type.REQUESTED, Style.HELP, XCode.BOLD +
                "---------------------------------------- " +
                "Help -----------------------------------------\n" + XCode.WEIGHT_OFF +
                "Interactive: java -jar Manager.jar\n" +
                "Single: java -jar Manager.jar <command>\n" +
                "Command: <flag(s)> <command(s)> <flag(s)>\n\n" + XCode.BOLD +
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
        Console.log(Type.REQUESTED, Style.HELP, XCode.BOLD +
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