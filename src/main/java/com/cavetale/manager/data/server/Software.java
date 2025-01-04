package com.cavetale.manager.data.server;

import com.cavetale.manager.data.DataException;
import com.cavetale.manager.data.Source;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Server software, used to register downloadable server software
 */
public enum Software {
    Paper("https://api.papermc.io/v2/projects/paper/versions/1.21.3/builds/66/downloads/paper-1.21.3-66.jar", "1.21.3-66", "PaperMC"); // TODO: Download newest version using Paper API

    private final @NotNull Source source;
    private final @NotNull String[] refs;

    private boolean selected = false;
    private final @NotNull List<String> installations = new LinkedList<>();

    Software(@NotNull String uri, @NotNull String version, @NotNull String @NotNull ... aliases) {
        this.source = new Source.Other(Util.uriOf(uri), version);
        this.refs = new String[aliases.length + 1];
        this.refs[0] = this.name();
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
    }

    @Override
    public @NotNull String toString() {
        return this.refs[0];
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public boolean isSelected() {
        return this.selected;
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

    public void install() {
        Console.log(Type.INFO, "Installing " + this.name() + " software");
        if (this.isInstalled()) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.name() + " software skipped (already installed)\n");
            }
            return;
        }
        try {
            String file = this.name() + "-" + source.version + ".jar";
            Util.download(this.source.uri, new File(Softwares.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Installing " + this.name() + " software failed (" + e.getMessage() + ")\n");
            }
        }
    }

    public void update() {
        Console.log(Type.INFO, "Updating " + this.name() + " software"); // Uninstall software
        File folder = Softwares.FOLDER;
        for (String file : this.installations) {
            if (new File(folder, file).delete()) continue;
            if (!Console.log(Type.INFO, Style.ERR, " failed - failed to delete " + file + "\n")) {
                Console.log(Type.ERR, "Updating " + this.name() + " software failed - failed to delete " + file + "\n");
            }
            return;
        }
        this.installations.clear();

        try { // Install software
            String file = this.name() + "-" + source.version + ".jar";
            Util.download(this.source.uri, new File(Softwares.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed - failed to download (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Updating " + this.name() + " software failed - failed to download (" + e.getMessage() + ")\n");
            }
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
