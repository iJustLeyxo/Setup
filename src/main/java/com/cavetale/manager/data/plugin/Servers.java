package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Parser;
import com.cavetale.manager.parser.container.ServerContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public final class Servers {
    // TODO: Only resolve when requested
    private static final @NotNull List<Server> selected = new LinkedList<>();
    private static final @NotNull List<Server> installed = new LinkedList<>();

    public static void reloadSelected(@NotNull Parser parser) {
        Console.log(Type.EXTRA, "Reloading selected servers\n");
        for (Server s : Server.values()) s.reset(); // Reset selections
        Servers.selected.clear();

        ServerContainer servers = (ServerContainer) Flag.server.container();
        if (Flag.installed.isSelected()) {
            for (Server s : Servers.installed) s.target();
            Console.log(Type.DEBUG, "Selecting installed servers\n");
        } else if (Flag.all.isSelected() || (Flag.server.isSelected() && servers.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all servers\n");
            for (Server s : Server.values()) s.target();
        } else {
            Console.log(Type.DEBUG, "Selecting servers " + servers.get() + "\n");
            for (Server s : servers.get()) s.target(); // Select by server
        }

        for (Server s : Server.values()) if (s.isSelected()) Servers.selected.add(s); // Update selection
    }

    public static void reloadInstallations() {
        Console.log(Type.EXTRA, "Reloading installed servers\n");
        Servers.installed.clear(); // Reset installations

        for (Server s : Server.values()) if (s.isInstalled()) Servers.installed.add(s); // Update installation
    }

    public static @NotNull List<Server> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<Server> servers = new LinkedList<>();
        for (Server s : Server.values()) {
            if ((installed == null || installed == s.isInstalled()) &&
                    (selected == null || selected == s.isSelected())) {
                servers.add(s);
            }
        }
        return servers;
    }

    public static @NotNull List<Server> installed() {
        return Servers.installed;
    }

    public static @NotNull List<Server> selected() {
        return Servers.selected;
    }

    public static void summarize() {
        if (!Servers.selected.isEmpty()) Servers.summarizeSelected();
        else if (!Servers.installed.isEmpty()) Servers.summarizeInstalled();
        else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SERVER, XCode.BOLD + "No servers selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Server> selected = Servers.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " server(s) selected", 4, 21, selected.toArray());
        selected = Servers.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " server(s) installed", 4, 21, selected.toArray());
        }
        selected = Servers.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " server(s) superfluous", 4, 21, selected.toArray());
        }
        selected = Servers.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " server(s) missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.INSTALL, Servers.installed.size() + " server(s) installed", 4, 21, Servers.installed.toArray());
    }

    public static void listSelected() {
        if (Servers.selected.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SERVER, XCode.BOLD + "No servers selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SERVER, Servers.selected.size() + " server(s) selected", 4, 21, Servers.selected.toArray());
    }

    public static void listInstalled() {
        if (Servers.installed.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SERVER, XCode.BOLD + "No servers installed\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SERVER, Servers.installed.size() + " server(s) installed", 4, 21, Servers.installed.toArray());
    }

    public static void list() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.SERVER, Server.values().length + " server(s) available", 4, 21, (Object[]) Server.values());
    }
}
