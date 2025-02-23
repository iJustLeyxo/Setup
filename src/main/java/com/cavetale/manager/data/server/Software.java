package com.cavetale.manager.data.server;

import com.cavetale.manager.data.DataException;
import com.cavetale.manager.data.Sel;
import com.cavetale.manager.download.Source;
import com.cavetale.manager.download.Ver;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

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

    // TODO: Custom printing
    private @NotNull Sel sel = Sel.NONE;
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

    // TODO: What?
    @Override
    public @NotNull String toString() {
        return this.refs[0];
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public boolean isTargeted() {
        return this.sel == Sel.TARGET;
    }

    public void select() {
        if (this.sel == Sel.NONE) this.sel = Sel.NORMAL;
    }

    public boolean isSelected() {
        return this.sel != Sel.NONE;
    }

    public void reset() {
        this.sel = Sel.NONE;
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
            Util.download(this.source.uri(), new File(Softwares.FOLDER, file));
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
        File folder = Softwares.FOLDER;
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
            Util.download(this.source.uri(), new File(Softwares.FOLDER, file));
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
        File folder = Softwares.FOLDER;
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
