package com.cavetale.manager.data;

import com.cavetale.manager.data.plugin.Plugin;
import com.cavetale.manager.download.Source;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public interface Installable {
    @NotNull String displayName();
    boolean isInstalled();
    @NotNull Source source();
    @NotNull File downloads();
    @NotNull List<String> installations();

    default void install() {
        Console.log(Type.INFO, "Installing " + this.displayName());
        if (this.isInstalled()) {
            Console.log(Type.INFO, Type.WARN, "Installing " + this.displayName(), " skipped (already installed)");
            return;
        }

        try {
            String name = this.displayName() + "-" + this.source().ver() + ".jar";
            Util.download(this.source().uri(), Plugin.FOLDER, name);
            this.installations().add(name);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            Console.log(Type.INFO, Type.ERR, "Installing " + this.displayName(), " failed (" + e.getMessage() + ")\n");
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    default void update() {
        Console.log(Type.INFO, "Updating " + this.displayName());

        String name = this.displayName() + "-" + this.source().ver() + ".jar";
        File file;

        // Stash download
        try {
            file = Util.stash(this.source().uri());
        } catch (IOException e) {
            Console.log(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to download (" + e.getMessage() + ")\n");
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
            return;
        }

        // Uninstall plugin
        for (String inst : this.installations()) {
            Console.log(Type.DEBUG, "Uninstalling " + inst);
            File f = new File(Plugin.FOLDER, inst);
            if (!Files.isSymbolicLink(f.toPath())) {
                if (f.delete()) continue;
                Console.log(Type.EXTRA, Type.ERR, "Updating " + this.displayName(), " failed - failed to delete " + f + "\n");
            } else if (!Console.log(Type.EXTRA, Style.ERR, " failed - skipped " + f + " (linked)\n")) {
                Console.log(Type.ERR, "Updating " + this.displayName() + " failed - skipped " + f + " (linked)\n");
            }
            return;
        }
        this.installations().clear();

        // Install stashed plugin
        try {
            Util.finalise(file, Plugin.FOLDER, name);
            this.installations().add(name);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            Console.log(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to download (" + e.getMessage() + ")\n");
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    default void uninstall() {
        for (String inst : this.installations()) {
            Console.log(Type.INFO, "Uninstalling " + inst);
            File file = new File(Plugin.FOLDER, inst);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) {
                    this.installations().remove(inst);
                    Console.log(Type.INFO, Style.DONE, " done\n");
                } else Console.log(Type.EXTRA, Type.ERR, "Uninstalling " + file, " failed\n");
            } else Console.log(Type.EXTRA, Type.WARN, "Uninstalling " + file, " skipped (linked)\n");
        }
    }
}
