package com.cavetale.setup.data;

import com.cavetale.setup.cmd.CustomFlag;
import com.cavetale.setup.cmd.CustomStyle;
import com.cavetale.setup.cmd.SoftwareContents;
import com.cavetale.setup.download.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static link.l_pf.cmdlib.shell.Code.Std.BOLD;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Server software, used to register downloadable server software. */
public enum ServerSoftware implements Installable {
    PAPER(Source.link("https://fill-data.papermc.io/v1/objects/5be84d9fc43181a72d5fdee7e3167824d9667bfc97b1bf9721713f9a971481ca/paper-1.21.11-88.jar", "1.21.11-88"), "PAPR", "PaperMC");

    private final @NotNull String[] refs;
    private final @NotNull Source source;

    private @Nullable Sel sel = null;
    private @Nullable List<String> inst = null;

    ServerSoftware(@NotNull Source source, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Data.capsToCamel(this.name());
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
        return ServerSoftware.FOLDER;
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
        if (this.inst == null) ServerSoftware.installed();
        return this.inst;
    }

    public boolean isInstalled() {
        return !this.installations().isEmpty();
    }

    //= Finder ==

    public static @NotNull ServerSoftware get(@NotNull String ref) throws NotFoundException {
        for (ServerSoftware s : ServerSoftware.values()) for (String r : s.refs) if (r.equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    public static @NotNull ServerSoftware get(@NotNull File file) throws NotASoftwareException, NotFoundException {
        String ref = file.getName().toLowerCase();
        if (!file.isFile() || !ref.endsWith(".jar")) throw new NotASoftwareException(file);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        int endStart = Math.min(verStart, extStart);
        ref = ref.substring(0, endStart);
        for (ServerSoftware s : ServerSoftware.values()) for (String r : s.refs) if (ref.equalsIgnoreCase(r)) return s;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    public static final @NotNull File FOLDER = new File("./");

    private static @Nullable List<ServerSoftware> selected = null;
    private static @Nullable List<ServerSoftware> installed = null;
    private static @Nullable List<String> unknown = null;

    public static @NotNull List<ServerSoftware> selected() {
        if (ServerSoftware.selected != null) return ServerSoftware.selected; // Update not necessary

        // Update selected
        STDIO.debug("Reloading selected software");
        for (ServerSoftware s : ServerSoftware.values()) s.deselect();
        ServerSoftware.selected = new LinkedList<>();

        SoftwareContents softwares = (SoftwareContents) CustomFlag.SOFTWARE.container();
        if (CustomFlag.INSTALLED.isSelected()) {
            STDIO.debug("Selecting installed software");
            for (ServerSoftware s : ServerSoftware.installed()) s.target();
        } else if (CustomFlag.ALL.isSelected() || (CustomFlag.SOFTWARE.isSelected() && softwares.isEmpty())) { // Select all
            STDIO.debug("Selecting all software");
            for (ServerSoftware s : ServerSoftware.values()) s.target();
        } else {
            STDIO.debug("Selecting servers ", softwares.contents());
            for (ServerSoftware s : softwares.contents()) s.target(); // Select by software
        }

        for (ServerSoftware s : ServerSoftware.values()) if (s.isSelected()) ServerSoftware.selected.add(s); // Update selection
        return ServerSoftware.selected;
    }

    public static @NotNull List<ServerSoftware> installed() {
        if (ServerSoftware.installed != null) return ServerSoftware.installed; // Update not necessary

        // Update installed
        ServerSoftware.loadInstalled();
        assert ServerSoftware.installed != null;
        return ServerSoftware.installed;
    }

    public static @NotNull List<String> unknown() {
        if (ServerSoftware.unknown != null) return ServerSoftware.unknown; // Update not necessary

        // Update unknown
        ServerSoftware.loadInstalled();
        assert ServerSoftware.unknown != null;
        return ServerSoftware.unknown;
    }

    private static void loadInstalled() {
        // Update installed
        STDIO.debug("Reloading installed software");
        for (ServerSoftware s : ServerSoftware.values()) s.inst = new LinkedList<>(); // Reset installations
        ServerSoftware.installed = new LinkedList<>();
        ServerSoftware.unknown = new LinkedList<>();
        // Scan installations
        File[] files = ServerSoftware.FOLDER.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.getName().startsWith("Setup")) continue;
            ServerSoftware s = null;
            try {
                s = ServerSoftware.get(f);
            } catch (ServerSoftware.NotASoftwareException e) {
                continue;
            } catch (NotFoundException ignored) {}
            if (s != null) s.installations().add(f.getName());
            else ServerSoftware.unknown.add(f.getName());
        }

        for (ServerSoftware s : ServerSoftware.values()) if (s.isInstalled()) ServerSoftware.installed.add(s); // Update installations
    }

    public static void reset() {
        STDIO.debug("Resetting software");
        for (ServerSoftware s : ServerSoftware.values()) s.revert();
        ServerSoftware.selected = null;
        ServerSoftware.installed = null;
        ServerSoftware.unknown = null;
    }

    public static @NotNull List<ServerSoftware> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<ServerSoftware> software = new LinkedList<>();
        for (ServerSoftware s : ServerSoftware.values()) {
            if ((installed == null || installed == s.isInstalled()) &&
                    (selected == null || selected == s.isSelected())) {
                software.add(s);
            }
        }
        return software;
    }

    //= Cosmetics ==

    public static void requestAll() {
        if (ServerSoftware.values().length == 0) {
            STDIO.log(CustomStyle.SOFTWARE, BOLD + "No software available");
            return;
        }

        STDIO.list(CustomStyle.SOFTWARE, ServerSoftware.values().length +
                " software available", (Object[]) ServerSoftware.values());
    }

    public static boolean listSelected() {
        List<ServerSoftware> selected = ServerSoftware.selected();
        if (selected.isEmpty()) return false;
        STDIO.list(CustomStyle.SOFTWARE, selected.size() +
                " software selected", selected.toArray());
        return true;
    }

    public static boolean listInstalled() {
        List<ServerSoftware> software = ServerSoftware.installed();
        if (software.isEmpty()) return false;
        STDIO.list(CustomStyle.SOFTWARE, software.size() +
                " software installed", software.toArray());
        return true;
    }

    public static void requestInstalled() {
        if (ServerSoftware.installed().isEmpty()) {
            STDIO.log(CustomStyle.SOFTWARE, BOLD + "No software installed");
            return;
        }

        STDIO.list(CustomStyle.SOFTWARE, ServerSoftware.installed().size() +
                " software installed", ServerSoftware.installed().toArray());
    }

    public static boolean listUnknown() {
        List<String> unknown = ServerSoftware.unknown();
        if (unknown.isEmpty()) return false;
        STDIO.list(CustomStyle.UNKNOWN, unknown.size() +
                " software unknown", unknown.toArray());
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
