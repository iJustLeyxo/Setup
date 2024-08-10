package com.cavetale.manager.command;

import com.cavetale.manager.data.plugin.Plugin;
import com.cavetale.manager.data.server.Software;
import com.cavetale.manager.parser.Command;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Result;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.EscCode;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Verbosity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;

public final class UninstallExec extends Exec {
    public UninstallExec(@NotNull Result result) {
        super(result);
    }

    @Override
    public void run() {
        if (!serverSoftware() && !plugins()) {
            Console.out(Style.INFO, "Nothing selected for uninstall\n\n");
        }
    }

    @Override
    public void help() {

    }

    private boolean serverSoftware() {
        if (!this.result.tokens().flags().containsKey(Flag.SOFTWARE)) {
            return false;
        }
        Set<Software> selected = this.result.serverSoftware().selected();
        Console.list(selected.size() + " software(s) selected to uninstall",
                selected, Style.WARN, EscCode.BLUE, 4, 21);
        for (Software s : selected) {
            Console.out(Style.INFO, "Uninstalling " + s.refs[0] + " server software");
            File file = s.file();
            if (file == null) {
                if (!Console.out(Style.INFO, Style.ERR, " skipped (unable to uninstall)")) {
                    Console.out(Style.ERR, "Uninstalling " + s.refs[0] + " server software skipped (unable to uninstall)");
                }
                continue;
            }
            if (!file.exists()) {
                if (!Console.out(Style.INFO, Style.WARN, " skipped (not installed)")) {
                    Console.out(Style.WARN, "Uninstalling " + s.refs[0] + " server software skipped (not installed)");
                }
                continue;
            }
            if (!file.delete()) {
                if (!Console.out(Style.INFO, Style.ERR, " failed")) {
                    Console.out(Style.ERR, "Uninstalling " + s.refs[0] + " failed");
                }
                continue;
            }
            Console.out(Style.INFO, Style.DONE, " done\n\n");
        }
        return true;
    }

    private boolean plugins() {
        Set<Plugin> selected;
        if (this.result.tokens().flags().containsKey(Flag.ALL)) {
            selected = this.result.pluginManager().get(true, null, null);
        } else {
            selected = this.result.pluginManager().get(null, true, null);
        }
        if (!selected.isEmpty()) {
            Console.list(selected.size() + " plugins(s) to uninstall",
                    selected, Verbosity.OVERRIDE, EscCode.YELLOW, 4, 21);
        } else {
            return false;
        }
        File folder = new File("plugins/");
        if (!this.result.tokens().flags().containsKey(Flag.FORCE)) {
            if (!Console.in("Proceed with uninstall (Y/n)?").equalsIgnoreCase("y")) {
                return true;
            }
        }
        for (Plugin p : selected) {
            Console.out(Style.INFO, "Uninstalling " + p.name);
            File file = new File(folder, p.name + ".jar");
            if (!file.exists()) {
                if (!Console.out(Style.INFO, Style.WARN, " skipped (not installed)\n")) {
                    Console.out(Style.WARN, "Uninstalling " + p.name + " skipped (not installed)\n");
                }
                continue;
            }
            if (Files.isSymbolicLink(file.toPath())) {
                if (!Console.out(Style.INFO, Style.WARN,
                        " skipped (file is a symbolic link, use " + Command.UNLINK + " to remove)\n")) {
                    Console.out(Style.WARN, "Uninstalling " + p.name +
                            " skipped (file is a symbolic link, use " + Command.UNLINK + " to remove)\n");
                }
                continue;
            }
            if (file.delete()) {
                Console.out(Style.INFO, Style.DONE, " done\n");
                continue;
            }
            if(!Console.out(Style.INFO, Style.ERR, " failed\n")) {
                Console.out(Style.ERR, "Uninstalling " + p.name + " failed\n");
            }
        }
        return true;
    }
}