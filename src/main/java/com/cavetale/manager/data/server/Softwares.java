package com.cavetale.manager.data.server;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Parser;
import com.cavetale.manager.parser.container.SoftwareContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public final class Softwares {
    // TODO: Only resolve when requested
    public static final @NotNull File FOLDER = new File("./");

    private static final @NotNull List<Software> selected = new LinkedList<>();
    private static final @NotNull List<Software> installed = new LinkedList<>();
    private static final @NotNull List<String> unknown = new LinkedList<>();

    public static void reloadSelected(@NotNull Parser parser) {
        Console.log(Type.EXTRA, "Reloading selected software\n");
        for (Software s : Software.values()) s.reset(); // Reset selections
        Softwares.selected.clear();

        SoftwareContainer softwares = (SoftwareContainer) Flag.SOFTWARE.container();
        if (Flag.INSTALLED.isSelected()) {
            Console.log(Type.DEBUG, "Selecting installed software\n");
            for (Software s : Softwares.installed) s.target();
        } else if (Flag.ALL.isSelected() || (Flag.SOFTWARE.isSelected() && softwares.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all software\n");
            for (Software s : Software.values()) s.target();
        } else {
            Console.log(Type.DEBUG, "Selecting servers " + softwares.get() + "\n");
            for (Software s : softwares.get()) s.target(); // Select by software
        }

        for (Software s : Software.values()) if (s.isSelected()) Softwares.selected.add(s); // Update selection
    }

    public static void reloadInstallations() {
        Console.log(Type.EXTRA, "Reloading installed software\n");
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
            Console.log(Type.REQUESTED, Style.SOFTWARE, XCode.BOLD + "No software selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Software> selected = Softwares.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " software selected", 4, 21, selected.toArray());
        selected = Softwares.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " software installed", 4, 21, selected.toArray());
        }
        selected = Softwares.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " software superfluous", 4, 21, selected.toArray());
        }
        selected = Softwares.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " software missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        Console.sep();
        List<Software> installed = Softwares.installed;
        installed.remove(null);
        Console.logL(Type.REQUESTED, Style.SOFTWARE, installed.size() +
                " software installed", 4, 21, installed.toArray());
        List<String> unknown = Softwares.unknown; // Always show unknown software
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " software unknown", 4, 21, unknown.toArray());
        }
    }

    public static void listSelected() {
        if (Softwares.selected.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SOFTWARE, XCode.BOLD + "No software selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, Softwares.selected.size() + " software selected", 4, 21, Softwares.selected.toArray());
    }

    public static void listInstalled() {
        if (Softwares.installed.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SOFTWARE, XCode.BOLD + "No software installed\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, Softwares.installed.size() + " software installed", 4, 21, Softwares.installed.toArray());
    }

    public static void list() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, Software.values().length + " software available", 4, 21, (Object[]) Software.values());
    }
}
