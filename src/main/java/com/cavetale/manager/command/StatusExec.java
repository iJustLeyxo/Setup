package com.cavetale.manager.command;

import com.cavetale.manager.data.plugin.Plugin;
import com.cavetale.manager.parser.Result;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.EscCode;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Verbosity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class StatusExec extends Exec {
    public StatusExec(@NotNull Result result) {
        super(result);
    }

    @Override
    public void run() {
        Set<Plugin> selected = this.result.pluginManager().get(null, true, null);
        if (!selected.isEmpty()) {
            Console.list(selected.size() + " plugins(s) selected",
                    selected, Style.INFO, EscCode.BLUE, 4, 21);
            Set<Plugin> plugins = this.result.pluginManager().get(true, true, false);
            if (!plugins.isEmpty()) {
                Console.list(plugins.size() + " plugins(s) installed (not linked)",
                        plugins, Verbosity.OVERRIDE, EscCode.GREEN, 4, 21);
            }
            plugins = this.result.pluginManager().get(true, true, true);
            if (!plugins.isEmpty()) {
                Console.list(plugins.size() + " plugins(s) installed (linked)",
                        plugins, Verbosity.OVERRIDE, EscCode.MAGENTA, 4, 21);
            }
            plugins = this.result.pluginManager().get(true, false, null);
            if (!plugins.isEmpty()) {
                Console.list(plugins.size() + " plugins(s) superfluous",
                        plugins, Verbosity.OVERRIDE, EscCode.YELLOW, 4, 21);
            }
            plugins = this.result.pluginManager().get(false, true, null);
            if (!plugins.isEmpty()) {
                Console.list(plugins.size() + " plugins(s) missing",
                        plugins, Verbosity.OVERRIDE, EscCode.RED,4, 21);
            }
        } else {
            Set<Plugin> unlinked = this.result.pluginManager().get(true, null, false);
            Set<Plugin> linked = this.result.pluginManager().get(true, null, true);
            if (!unlinked.isEmpty() || !linked.isEmpty()) {
                if (!unlinked.isEmpty()) {
                    Console.list(unlinked.size() + " plugins(s) installed (not linked)",
                            unlinked, Verbosity.OVERRIDE, EscCode.GREEN, 4, 21);
                }
                if (!linked.isEmpty()) {
                    Console.list(linked.size() + " plugins(s) installed (linked)",
                            linked, Verbosity.OVERRIDE, EscCode.MAGENTA, 4, 21);
                }
            }
        }
        Set<String> unknown = this.result.pluginManager().unknown();
        if (!unknown.isEmpty()) {
            Console.list(unknown.size() + " plugins(s) unknown",
                    unknown, Verbosity.OVERRIDE, EscCode.GRAY, 4, 21);
        }
    }

    @Override
    public void help() {

    }
}