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
import java.util.Set;

/**
 * Server software, used to register downloadable server software
 */
public enum Software {
    PAPER("https://api.papermc.io/v2/projects/paper/versions/1.21/builds/127/downloads/paper-1.21-127.jar", "127",
            "Paper", "PaperMC"); // TODO: Download newest version using Paper API

    public final @NotNull String[] refs;
    public final @NotNull Source source;

    Software(@NotNull String uri, @NotNull String version, @NotNull String ref, @NotNull String... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = ref;
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
        this.source = new Source.Other(Util.uriOf(uri), version);
    }

    public void install() {
        Console.log(Type.INFO, "Installing " + this.refs[0] + " software");
        File file = new File(this.refs[0] + "-" + source.version + ".jar");
        if (SoftwareIndexer.active.getInstalled().containsKey(this)) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n"))
                Console.log(Type.WARN, "Installing " + this.name() + " software skipped (already installed)\n");
            return;
        }
        try {
            Util.download(this.source.uri, file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n"))
                Console.log(Type.ERR, "Installing " + this.refs[0] + " software failed (" + e.getMessage() + ")\n");
        }
    }

    public void update() {
        this.uninstall();
        this.install();
    }

    public void uninstall() {
        Set<File> files = SoftwareIndexer.active.installed.get(this);
        if (files == null) return;
        File folder = new File(".");
        for (File f : files) {
            Console.log(Type.INFO, "Uninstalling " + f.getName() + " software");
            if (new File(folder, f.getName()).delete()) {
                Console.log(Type.INFO, Style.DONE, " done\n");
                continue;
            }
            if (!Console.log(Type.INFO, Style.ERR, " failed\n"))
                Console.log(Type.ERR, "Uninstalling " + f.getName() + " software failed\n");
        }
    }

    @Override
    public @NotNull String toString() {
        return this.refs[0];
    }

    public static @NotNull Software get(@NotNull String ref) throws SoftwareNotFoundException {
        String lowRef = ref.toLowerCase();
        for (Software s : Software.values()) {
            for (String r : s.refs) {
                if (lowRef.equalsIgnoreCase(r)) return s;
            }
        }
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
        for (Software s : Software.values()) {
            for (String r : s.refs) {
                if (ref.equalsIgnoreCase(r)) return s;
            }
        }
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
