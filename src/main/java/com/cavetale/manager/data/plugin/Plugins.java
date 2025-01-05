package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Parser;
import com.cavetale.manager.parser.container.PluginContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class Plugins {
    public static final @NotNull File FOLDER = new File("plugins/");

    private static final @NotNull List<Plugin> selected = new LinkedList<>();
    private static final @NotNull List<Plugin> installed = new LinkedList<>();
    private static final @NotNull List<String> unknown = new LinkedList<>();

    public static void reloadSelected(@NotNull Parser parser) {
        Console.log(Type.EXTRA, "Reloading selected plugins\n");
        for (Plugin p : Plugin.values()) p.setSelected(false); // Reset selections
        Plugins.selected.clear();

        PluginContainer plugins = (PluginContainer) Flag.plugin.container();
        if (Flag.installed.isSelected()) {
            Console.log(Type.DEBUG, "Selecting installed plugins");
            for (Plugin p : Plugins.installed) p.setSelected(true);
        } else if (Flag.all.isSelected() ||  (Flag.plugin.isSelected() && plugins.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all plugins");
            for (Plugin p : Plugin.values()) p.setSelected(true);
        } else {
            Console.log(Type.DEBUG, "Selecting plugins " + plugins.get());
            for (Plugin p : plugins.get()) p.setSelected(true); // Select by plugin

            for (Category c : Category.values()) if (c.isSelected()) for (Plugin p : c.plugins()) p.setSelected(true); // Select by category

            for (Server s : Server.values()) if (s.isSelected()) for (Plugin p : s.plugins()) p.setSelected(true); // Select by server
        }

        for (Plugin p : Plugin.values()) if (p.isSelected()) Plugins.selected.add(p); // Update selection
    }

    public static void reloadInstallations() {
        Console.log(Type.EXTRA, "Reloading installed plugins");
        for (Plugin p : Plugin.values()) p.clearInstallations(); // Reset installations
        Plugins.installed.clear();
        Plugins.unknown.clear();
        File folder = new File("plugins/"); // Scan installations
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files) {
            Plugin p = null;
            try {
                p = Plugin.get(f);
            } catch (Plugin.NotAPluginException e) {
                continue;
            } catch (Plugin.PluginNotFoundException ignored) {}
            if (p == null) {
                Plugins.unknown.add(f.getName());
                Console.log(Type.EXTRA, Style.WARN, "Unknown plugin " + f.getName());
            }
            else p.addInstallation(f.getName());
        }

        for (Plugin p : Plugin.values()) if (p.isInstalled()) Plugins.installed.add(p); // Update installation
    }

    public static @NotNull List<Plugin> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<Plugin> plugins = new LinkedList<>();
        for (Plugin p : Plugin.values()) {
            if ((installed == null || installed == p.isInstalled()) &&
                    (selected == null || selected == p.isSelected())) {
                plugins.add(p);
            }
        }
        return plugins;
    }

    public static @NotNull List<Plugin> installed() {
        return Plugins.installed;
    }

    public static @NotNull List<Plugin> selected() {
        return Plugins.selected;
    }

    public static @NotNull List<String> unknown() {
        return Plugins.unknown;
    }

    public static void summarize() {
        if (!Plugins.selected.isEmpty()) Plugins.summarizeSelected();
        else if (!Plugins.installed.isEmpty()) Plugins.summarizeInstalled();
        else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.PLUGIN, XCode.BOLD + "No plugins selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Plugin> selected = Plugins.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " plugin(s) selected", 4, 21, selected.toArray());
        selected = Plugins.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " plugin(s) installed", 4, 21, selected.toArray());
        }
        selected = Plugins.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " plugin(s) superfluous", 4, 21, selected.toArray());
        }
        selected = Plugins.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " plugin(s) missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        List<Plugin> installed = Plugins.installed;
        installed.remove(null);
        Console.sep();
        Console.logL(Type.REQUESTED, Style.INSTALL, installed.size() +
                " plugin(s) installed", 4, 21, installed.toArray());
        List<String> unknown = Plugins.unknown;
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " plugin(s) unknown", 4, 21, unknown.toArray());
        }
    }

    public static void listSelected() {
        if (Plugins.selected.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.PLUGIN, XCode.BOLD + "No plugins selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.PLUGIN, Plugins.selected.size() + " plugin(s) selected", 4, 21, Plugins.selected.toArray());
    }

    public static void listInstalled() {
        if (Plugins.installed.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.PLUGIN, XCode.BOLD + "No plugins installed\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.PLUGIN, Plugins.installed.size() + " plugin(s) installed", 4, 21, Plugins.installed.toArray());
    }

    public static void list() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.PLUGIN, Plugin.values().length + " plugin(s) available", 4, 21, (Object[]) Plugin.values());
    }
}
