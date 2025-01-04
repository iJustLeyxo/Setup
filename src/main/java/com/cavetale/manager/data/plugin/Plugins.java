package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Tokens;
import com.cavetale.manager.parser.container.CategoryContainer;
import com.cavetale.manager.parser.container.PluginContainer;
import com.cavetale.manager.parser.container.ServerContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class Plugins {
    public static final @NotNull File FOLDER = new File("./plugins/");

    private static final @NotNull List<Plugin> selected = new LinkedList<>();
    private static final @NotNull List<Plugin> installed = new LinkedList<>();
    private static final @NotNull List<String> unknown = new LinkedList<>();

    public static void reload(@NotNull Tokens tokens) {
        Plugins.reloadSelected(tokens);
        Plugins.reloadInstallations();
    }

    private static void reloadSelected(@NotNull Tokens tokens) {
        Console.log(Type.DEBUG, "Reloading selected plugins");
        PluginContainer plugins = (PluginContainer) tokens.flags().get(Flag.plugin);
        if (tokens.flags().containsKey(Flag.all) || (plugins != null && plugins.isEmpty())) { // Select all
            for (Plugin p : Plugin.values()) p.setSelected(true);
            return;
        }

        for (Plugin p : Plugin.values()) p.setSelected(false); // Reset selections

        if (plugins != null) for (Plugin p : plugins.get()) p.setSelected(true); // Select by plugin

        CategoryContainer categories = (CategoryContainer) tokens.flags().get(Flag.category); // Select by category
        if (categories != null) for (Category c : categories.get()) for (Plugin p : c.plugins()) p.setSelected(true);

        ServerContainer servers = (ServerContainer) tokens.flags().get(Flag.server); // Select by server
        if (servers != null) for (Server s : servers.get()) for (Plugin p : s.plugins()) p.setSelected(true);

        Plugins.selected.clear(); // Update selection
        for (Plugin p : Plugin.values()) if (p.isSelected()) Plugins.selected.add(p);
    }

    private static void reloadInstallations() {
        Console.log(Type.DEBUG, "Reloading installed plugins");
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
            if (p == null) Plugins.unknown.add(f.getName());
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
            Console.log(Type.REQUESTED, Style.INFO, "No plugins selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Plugin> selected = Plugins.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " plugins(s) selected", 4, 21, selected.toArray());
        selected = Plugins.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " plugins(s) installed", 4, 21, selected.toArray());
        }
        selected = Plugins.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " plugins(s) superfluous", 4, 21, selected.toArray());
        }
        selected = Plugins.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " plugins(s) missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        List<Plugin> installed = Plugins.installed;
        installed.remove(null);
        Console.sep();
        Console.logL(Type.REQUESTED, Style.INSTALL, installed.size() +
                " plugins(s) installed", 4, 21, installed.toArray());
        List<String> unknown = Plugins.unknown;
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " plugins(s) unknown", 4, 21, unknown.toArray());
        }
    }

    public static void listSelected() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.PLUGIN, "Plugins", 4, 21, Plugins.selected.toArray());
    }
}
