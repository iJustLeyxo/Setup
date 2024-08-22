package com.cavetale.manager.data.server;

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
            "Paper", "PaperMC");

    // TODO: Add individual install and update logic

    public final @NotNull String[] refs;
    public final @NotNull Source source;

    Software(@NotNull String uri, @NotNull String version, @NotNull String ref, @NotNull String... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = ref;
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
        this.source = new Source.Other(Util.uriOf(uri), version);
    }

    public void install(@NotNull Set<Software> installed) {
        Console.log(Type.INFO, "Installing " + this.refs[0] + " server software");
        File file = new File(this.refs[0] + "-" + source.version + ".jar");
        if (installed.contains(this)) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.name() + " skipped (already installed)\n");
            }
            return;
        }
        try {
            Util.download(this.source.uri, file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed\n")) {
                Console.log(Type.ERR, "Installing " + this.refs[0] + " server software failed\n");
            }
        }
    }

    public void update(@NotNull Set<Software> installed) {
        this.uninstall();
        this.install(installed);
    }

    public void uninstall() {
        File folder = new File("plugins/");
        File[] files = folder.listFiles();
        if (files == null) return;
        String name;
        for (File f : files) {
            name = f.getName();
            if (name.toLowerCase().startsWith(this.refs[0].toLowerCase()) && name.toLowerCase().endsWith(".jar")) {
                Console.log(Type.INFO, "Uninstalling " + name);
                if (f.delete()) {
                    Console.log(Type.INFO, Style.DONE, " done\n");
                    continue;
                }
                Console.log(Type.ERR, "Uninstalling " + this.name() + " failed\n");
            }
        }
    }

    @Override
    public @NotNull String toString() {
        return this.refs[0];
    }

    public static @NotNull Software get(@NotNull String ref) throws NotFoundException {
        for (Software s : Software.values()) {
            for (String r : s.refs) {
                if (ref.split(".")[0].split("-")[0].equalsIgnoreCase(r) &&
                        ref.toLowerCase().endsWith(".jar")) return s;
            }
        }
        throw new NotFoundException(ref);
    }

    public static void list() {
        Console.logL(Type.REQUESTED, Style.SOFTWARE, "Server software", 4, 21,
                (Object[]) Software.values());
    }

    public static final class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Server software \"" + ref + "\" not found");
        }
    }
}
