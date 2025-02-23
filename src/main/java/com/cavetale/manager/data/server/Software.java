package com.cavetale.manager.data.server;

import com.cavetale.manager.data.DataException;
import com.cavetale.manager.data.Sel;
import com.cavetale.manager.download.Source;
import com.cavetale.manager.download.Ver;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.parser.Parser;
import com.cavetale.manager.parser.container.SoftwareContainer;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Code;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 * Server software, used to register downloadable server software
 */
public enum Software {
    PAPER(Util.uriOf("https://api.papermc.io/v2/projects/paper/versions/1.21.4/builds/121/downloads/paper-1.21.4-121.jar"), Ver.of("1.21.4-122"), "PaperMC"); // TODO: Download newest version using Paper API

    private final @NotNull String[] refs;
    private final @NotNull Source source;

    private @NotNull Sel sel = Sel.OFF;
    private final @NotNull List<String> installations = new LinkedList<>();

    Software(@NotNull URI uri, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Source.Other(uri, ver);
    }

    public @NotNull String displayName() {
        return this.refs[0];
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public boolean isTargeted() {
        return this.sel == Sel.TARGET;
    }

    public void select() {
        if (this.sel == Sel.OFF) this.sel = Sel.ON;
    }

    public boolean isSelected() {
        return this.sel != Sel.OFF;
    }

    public void reset() {
        this.sel = Sel.OFF;
    }

    public void clearInstallations() {
        this.installations.clear();
    }

    public void addInstallation(@NotNull String file) {
        this.installations.add(file);
    }

    public boolean isInstalled() {
        return !this.installations.isEmpty();
    }

    public @NotNull List<String> installations() {
        return this.installations;
    }

    public void install() {
        Console.log(Type.INFO, "Installing " + this.displayName() + " software");
        if (this.isInstalled()) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.displayName() + " software skipped (already installed)\n");
            }
            return;
        }
        try {
            String file = this.displayName() + "-" + this.source.ver() + ".jar";
            Util.download(this.source.uri(), new File(Software.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Installing " + this.displayName() + " software failed (" + e.getMessage() + ")\n");
            }
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    public void update() {
        Console.log(Type.INFO, "Updating " + this.displayName() + " software"); // Uninstall software
        File folder = Software.FOLDER;
        for (String file : this.installations) {
            if (new File(folder, file).delete()) continue;
            if (!Console.log(Type.INFO, Style.ERR, " failed - failed to delete " + file + "\n")) {
                Console.log(Type.ERR, "Updating " + this.displayName() + " software failed - failed to delete " + file + "\n");
            }
            return;
        }
        this.installations.clear();

        try { // Install software
            String file = this.displayName() + "-" + source.ver() + ".jar";
            Util.download(this.source.uri(), new File(Software.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed - failed to download (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Updating " + this.displayName() + " software failed - failed to download (" + e.getMessage() + ")\n");
            }
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    public void uninstall() {
        File folder = Software.FOLDER;
        for (String f : this.installations) {
            Console.log(Type.INFO, "Uninstalling " + f + " software");
            if (new File(folder, f).delete()) {
                Console.log(Type.INFO, Style.DONE, " done\n");
                continue;
            }
            if (!Console.log(Type.INFO, Style.ERR, " failed\n")) {
                Console.log(Type.ERR, "Uninstalling " + f + " software failed\n");
            }
        }
        this.installations.clear();
    }

    //= Static ==

    // TODO: Only resolve when requested
    public static final @NotNull File FOLDER = new File("./");

    private static final @NotNull List<Software> selected = new LinkedList<>();
    private static final @NotNull List<Software> installed = new LinkedList<>();
    private static final @NotNull List<String> unknown = new LinkedList<>();

    public static @NotNull Software get(@NotNull String ref) throws SoftwareNotFoundException {
        for (Software s : Software.values()) for (String r : s.refs) if (r.equalsIgnoreCase(ref)) return s;
        throw new SoftwareNotFoundException(ref);
    }

    public static @NotNull Software get(@NotNull File file) throws NotASoftwareException, SoftwareNotFoundException {
        String ref = file.getName().toLowerCase();
        if (!file.isFile() || !ref.endsWith(".jar")) throw new NotASoftwareException(file);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        int endStart = Math.min(verStart, extStart);
        ref = ref.substring(0, endStart);
        for (Software s : Software.values()) for (String r : s.refs) if (ref.equalsIgnoreCase(r)) return s;
        throw new SoftwareNotFoundException(ref);
    }

    public static void reloadSelected(@NotNull Parser parser) {
        Console.log(Type.EXTRA, "Reloading selected software\n");
        for (Software s : Software.values()) s.reset(); // Reset selections
        Software.selected.clear();

        SoftwareContainer softwares = (SoftwareContainer) Flag.SOFTWARE.container();
        if (Flag.INSTALLED.isSelected()) {
            Console.log(Type.DEBUG, "Selecting installed software\n");
            for (Software s : Software.installed) s.target();
        } else if (Flag.ALL.isSelected() || (Flag.SOFTWARE.isSelected() && softwares.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all software\n");
            for (Software s : Software.values()) s.target();
        } else {
            Console.log(Type.DEBUG, "Selecting servers " + softwares.get() + "\n");
            for (Software s : softwares.get()) s.target(); // Select by software
        }

        for (Software s : Software.values()) if (s.isSelected()) Software.selected.add(s); // Update selection
    }

    public static void reloadInstallations() {
        Console.log(Type.EXTRA, "Reloading installed software\n");
        for (Software s : Software.values()) s.clearInstallations(); // Reset installations
        Software.installed.clear();
        Software.unknown.clear();
        // Scan installations
        File[] files = Software.FOLDER.listFiles();
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
            else Software.unknown.add(f.getName());
        }

        for (Software s : Software.values()) if (s.isInstalled()) Software.installed.add(s); // Update installations
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
        return Software.installed;
    }

    public static @NotNull List<Software> selected() {
        return Software.selected;
    }

    public static @NotNull List<String> unknown() {
        return Software.unknown;
    }

    public static void summarize() {
        if (!Software.selected.isEmpty()) Software.summarizeSelected(); // Compare selected to installed software
        else if (!Software.installed().isEmpty()) Software.summarizeInstalled(); // Show installed software if nothing is selected
        else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SOFTWARE, Code.BOLD + "No software selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Software> selected = Software.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " software selected", 4, 21, selected.toArray());
        selected = Software.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " software installed", 4, 21, selected.toArray());
        }
        selected = Software.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " software superfluous", 4, 21, selected.toArray());
        }
        selected = Software.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " software missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        Console.sep();
        List<Software> installed = Software.installed;
        installed.remove(null);
        Console.logL(Type.REQUESTED, Style.SOFTWARE, installed.size() +
                " software installed", 4, 21, installed.toArray());
        List<String> unknown = Software.unknown; // Always show unknown software
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " software unknown", 4, 21, unknown.toArray());
        }
    }

    public static void listSelected() {
        if (Software.selected.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SOFTWARE, Code.BOLD + "No software selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, Software.selected.size() + " software selected", 4, 21, Software.selected.toArray());
    }

    public static void listInstalled() {
        if (Software.installed.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SOFTWARE, Code.BOLD + "No software installed\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, Software.installed.size() + " software installed", 4, 21, Software.installed.toArray());
    }

    public static void list() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, Software.values().length + " software available", 4, 21, (Object[]) Software.values());
    }

    public static final class SoftwareNotFoundException extends InputException {
        public SoftwareNotFoundException(@NotNull String ref) {
            super("Server software \"" + ref + "\" not found");
        }
    }

    public static class NotASoftwareException extends DataException {
        public NotASoftwareException(@NotNull File file) {
            super(file.getName() + " is not a software");
        }
    }
}
