package com.cavetale.manager.data.server;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Tokens;
import com.cavetale.manager.parser.container.SoftwareContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public final class Softwares {
    public static final @NotNull File FOLDER = new File("./");

    private static final @NotNull List<Software> selected = new LinkedList<>();
    private static final @NotNull List<Software> installed = new LinkedList<>();
    private static final @NotNull List<String> unknown = new LinkedList<>();

    public static void reload(@NotNull Tokens tokens) {
        Softwares.reloadSelected(tokens);
        Softwares.reloadInstallations();
    }

    private static void reloadSelected(@NotNull Tokens tokens) {
        Console.log(Type.DEBUG, "Reloading selected software");
        SoftwareContainer softCon = (SoftwareContainer) tokens.flags().get(Flag.software);
        if (tokens.flags().containsKey(Flag.all) || (softCon != null && softCon.isEmpty())) { // Select all
            for (Software s : Software.values()) s.setSelected(true);
            return;
        }

        for (Software s : Software.values()) s.setSelected(false); // Reset selections

        if (softCon != null) for (Software s : softCon.get()) s.setSelected(true); // Select by software
    }

    private static void reloadInstallations() {
        Console.log(Type.DEBUG, "Reloading installed software");
        for (Software s : Software.values()) s.clearInstallations(); // Reset installations
        Softwares.installed.clear();
        Softwares.unknown.clear();
        // Scan installations
        File[] files = Softwares.FOLDER.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.getName().startsWith("Setup")) continue;
            Software s = null;
            try {
                s = Software.get(f);
            } catch (Software.NotASoftwareException e) {
                continue;
            } catch (Software.SoftwareNotFoundException ignored) {}
            if (s != null) s.addInstallation(f.getName());
            else Softwares.unknown.add(f.getName());
        }

        for (Software s : Software.values()) if (s.isInstalled()) Softwares.installed.add(s); // Update installations
    }

    public static @NotNull List<Software> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<Software> software = new LinkedList<>();
        for (Software s : Software.values()) {
            if ((installed == null || installed == s.isInstalled()) &&
                    (selected == null || selected == s.isSelected())) {
                software.add(s);
            }
        }
        return software;
    }

    public static @NotNull List<Software> installed() {
        return Softwares.installed;
    }

    public static @NotNull List<Software> selected() {
        return Softwares.selected;
    }

    public static @NotNull List<String> unknown() {
        return Softwares.unknown;
    }

    public static void summarize() {
        if (!Softwares.selected.isEmpty()) Softwares.summarizeSelected(); // Compare selected to installed software
        else if (!Softwares.installed().isEmpty()) Softwares.summarizeInstalled(); // Show installed software if nothing is selected
        else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.INFO, "No software selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Software> selected = Softwares.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " software(s) selected", 4, 21, selected.toArray());
        selected = Softwares.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " software(s) installed", 4, 21, selected.toArray());
        }
        selected = Softwares.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " software(s) superfluous", 4, 21, selected.toArray());
        }
        selected = Softwares.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " software(s) missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        Console.sep();
        List<Software> installed = Softwares.installed;
        installed.remove(null);
        Console.logL(Type.REQUESTED, Style.SOFTWARE, installed.size() +
                " software(s) installed", 4, 21, installed.toArray());
        List<String> unknown = Softwares.unknown; // Always show unknown software
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " software(s) unknown", 4, 21, unknown.toArray());
        }
    }

    public static void listSelected() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, "Server software", 4, 21, Softwares.selected.toArray());
    }
}
