package com.cavetale.setup.data.server;

import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.console.CustomStyle;
import com.cavetale.setup.console.container.SoftwareContents;
import com.cavetale.setup.data.DataException;
import com.cavetale.setup.data.Installable;
import com.cavetale.setup.data.Sel;
import com.cavetale.setup.download.Source;
import com.cavetale.setup.util.Util;
import link.l_pf.cmdlib.io.Code;
import link.l_pf.cmdlib.io.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static link.l_pf.cmdlib.io.Console.SYSIO;

/** Server software, used to register downloadable server software. */
public enum Software implements Installable {
    PAPER(new Source("https://api.papermc.io/v2/projects/paper/versions/1.21.4/builds/121/downloads/paper-1.21.4-121.jar", "1.21.4-122"), "PaperMC"); // TODO: Download newest version using Paper API

    private final @NotNull String[] refs;
    private final @NotNull Source source;

    private @Nullable Sel sel = null;
    private @Nullable List<String> inst = null;

    Software(@NotNull Source source, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = source;
    }

    @Override
    public @NotNull String displayName() {
        return this.refs[0];
    }

    @Override
    public @NotNull Source source() {
        return this.source;
    }

    @Override
    public @NotNull File downloads() {
        return Software.FOLDER;
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    public void revert() {
        this.sel = null;
        this.inst = null;
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public void select() {
        if (this.sel != Sel.TARGET) this.sel = Sel.ON;
    }

    public void deselect() {
        this.sel = Sel.OFF;
    }

    public boolean isSelected() {
        return this.sel != Sel.OFF;
    }

    //= Installation ==

    public @NotNull List<String> installations() {
        if (this.inst == null) Software.installed();
        return this.inst;
    }

    public boolean isInstalled() {
        return !this.installations().isEmpty();
    }

    //= Finder ==

    public static @NotNull Software get(@NotNull String ref) throws NotFoundException {
        for (Software s : Software.values()) for (String r : s.refs) if (r.equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    public static @NotNull Software get(@NotNull File file) throws NotASoftwareException, NotFoundException {
        String ref = file.getName().toLowerCase();
        if (!file.isFile() || !ref.endsWith(".jar")) throw new NotASoftwareException(file);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        int endStart = Math.min(verStart, extStart);
        ref = ref.substring(0, endStart);
        for (Software s : Software.values()) for (String r : s.refs) if (ref.equalsIgnoreCase(r)) return s;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    public static final @NotNull File FOLDER = new File("./");

    private static @Nullable List<Software> selected = null;
    private static @Nullable List<Software> installed = null;
    private static @Nullable List<String> unknown = null;

    public static @NotNull List<Software> selected() {
        if (Software.selected != null) return Software.selected; // Update not necessary

        // Update selected
        SYSIO.debug("Reloading selected software\n");
        for (Software s : Software.values()) s.deselect();
        Software.selected = new LinkedList<>();

        SoftwareContents softwares = (SoftwareContents) CustomFlag.SOFTWARE.container();
        if (CustomFlag.INSTALLED.isSelected()) {
            SYSIO.debug("Selecting installed software\n");
            for (Software s : Software.installed()) s.target();
        } else if (CustomFlag.ALL.isSelected() || (CustomFlag.SOFTWARE.isSelected() && softwares.isEmpty())) { // Select all
            SYSIO.debug("Selecting all software\n");
            for (Software s : Software.values()) s.target();
        } else {
            SYSIO.debug("Selecting servers " + softwares.contents() + "\n");
            for (Software s : softwares.contents()) s.target(); // Select by software
        }

        for (Software s : Software.values()) if (s.isSelected()) Software.selected.add(s); // Update selection
        return Software.selected;
    }

    public static @NotNull List<Software> installed() {
        if (Software.installed != null) return Software.installed; // Update not necessary

        // Update installed
        Software.loadInstalled();
        assert Software.installed != null;
        return Software.installed;
    }

    public static @NotNull List<String> unknown() {
        if (Software.unknown != null) return Software.unknown; // Update not necessary

        // Update unknown
        Software.loadInstalled();
        assert Software.unknown != null;
        return Software.unknown;
    }

    private static void loadInstalled() {
        // Update installed
        SYSIO.debug("Reloading installed software\n");
        for (Software s : Software.values()) s.inst = new LinkedList<>(); // Reset installations
        Software.installed = new LinkedList<>();
        Software.unknown = new LinkedList<>();
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
            } catch (NotFoundException ignored) {}
            if (s != null) s.installations().add(f.getName());
            else Software.unknown.add(f.getName());
        }

        for (Software s : Software.values()) if (s.isInstalled()) Software.installed.add(s); // Update installations
    }

    public static void reset() {
        SYSIO.debug("Resetting software\n");
        for (Software s : Software.values()) s.revert();
        Software.selected = null;
        Software.installed = null;
        Software.unknown = null;
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

    //= Cosmetics ==

    public static void requestAll() {
        SYSIO.separate();

        if (Software.values().length == 0) {
            SYSIO.requested(CustomStyle.SOFTWARE.toString() + Code.BOLD + "No software available\n");
            return;
        }

        SYSIO.list(Type.REQUESTED, CustomStyle.SOFTWARE, Software.values().length +
                " software available", 4, 21, (Object[]) Software.values());
    }

    public static boolean listSelected() {
        List<Software> selected = Software.selected();
        if (selected.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.SOFTWARE, selected.size() +
                " software selected", 4, 21, selected.toArray());
        return true;
    }

    public static boolean listInstalled() {
        List<Software> software = Software.installed();
        if (software.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.SOFTWARE, software.size() +
                " software installed", 4, 21, software.toArray());
        return true;
    }

    public static void requestInstalled() {
        SYSIO.separate();

        if (Software.installed().isEmpty()) {
            SYSIO.requested(CustomStyle.SOFTWARE.toString() + Code.BOLD + "No software installed\n");
            return;
        }

        SYSIO.list(Type.REQUESTED, CustomStyle.SOFTWARE, Software.installed().size() +
                " software installed", 4, 21, Software.installed().toArray());
    }

    public static boolean listUnknown() {
        List<String> unknown = Software.unknown();
        if (unknown.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.UNKNOWN, unknown.size() +
                " software unknown", 4, 21, unknown.toArray());
        return true;
    }

    public static final class NotFoundException extends Exception {
        public NotFoundException(@NotNull String ref) {
            super("Server software \"" + ref + "\" not found");
        }
    }

    public static class NotASoftwareException extends DataException {
        public NotASoftwareException(@NotNull File file) {
            super(file.getName() + " is not a software");
        }
    }
}
