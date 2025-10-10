package com.cavetale.setup.data;

import com.cavetale.setup.download.Source;
import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.util.Util;
import com.cavetale.setup.console.CustomStyle;
import io.github.ijustleyxo.jclix.app.Flag;
import io.github.ijustleyxo.jclix.io.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

public interface Installable {
    @NotNull String displayName();
    boolean isInstalled();
    @NotNull Source source();
    @NotNull File downloads();
    @NotNull List<String> installations();

    default void install() {
        SYSIO.info("Installing " + this.displayName());
        if (this.isInstalled()) {
            SYSIO.out(Type.INFO, Type.WARN, "Installing " + this.displayName(), " skipped (already installed)");
            return;
        }

        try {
            String name = this.displayName() + "-" + this.source().ver() + ".jar";
            Util.download(this.source().uri(), this.downloads(), name);
            this.installations().add(name);
            SYSIO.info(CustomStyle.DONE + " done\n");
        } catch (IOException e) {
            SYSIO.out(Type.INFO, Type.ERR, "Installing " + this.displayName(), " failed (" + e.getMessage() + ")\n");
            if (Flag.Default.ERROR.isSelected()) SYSIO.outT(Type.HELP, e);
        }
    }

    default void update() {
        SYSIO.info("Updating " + this.displayName());

        String name = this.displayName() + "-" + this.source().ver() + ".jar";
        File file;

        // Stash download
        try {
            file = Util.stash(this.source().uri());
        } catch (IOException e) {
            SYSIO.out(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to download (" + e.getMessage() + ")\n");
            if (Flag.Default.ERROR.isSelected()) SYSIO.outT(Type.HELP, e);
            return;
        }

        // Uninstall
        for (String inst : this.installations()) {
            SYSIO.debug("Uninstalling " + inst);
            File f = new File(this.downloads(), inst);
            if (!Files.isSymbolicLink(f.toPath())) {
                if (f.delete()) continue;
                SYSIO.out(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to delete " + f + "\n");
            } else if (!SYSIO.info(CustomStyle.ERR + " failed - skipped " + f + " (linked)\n")) {
                SYSIO.err("Updating " + this.displayName() + " failed - skipped " + f + " (linked)\n");
            }
            return;
        }
        this.installations().clear();

        // Install stashed
        try {
            Util.finalise(file, this.downloads(), name);
            this.installations().add(name);
            SYSIO.info(CustomStyle.DONE + " done\n");
        } catch (IOException e) {
            SYSIO.out(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to download (" + e.getMessage() + ")\n");
            if (Flag.Default.ERROR.isSelected()) SYSIO.outT(Type.HELP, e);
        }
    }

    default void uninstall() {
        for (String inst : this.installations()) {
            SYSIO.info("Uninstalling " + inst);
            File file = new File(this.downloads(), inst);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) {
                    this.installations().remove(inst);
                    SYSIO.info(CustomStyle.DONE + " done\n");
                } else SYSIO.out(Type.INFO, Type.ERR, "Uninstalling " + file, " failed\n");
            } else SYSIO.out(Type.INFO, Type.WARN, "Uninstalling " + file, " skipped (linked)\n");
        }
    }
}
