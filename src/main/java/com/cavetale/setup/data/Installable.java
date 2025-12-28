package com.cavetale.setup.data;

import com.cavetale.setup.Setup;
import com.cavetale.setup.download.Source;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Interface for software that can be installed, uninstalled and updated. */
public interface Installable {
    @NotNull String displayName();
    boolean isInstalled();
    @NotNull Source source();
    @NotNull File downloads();
    @NotNull List<String> installations();

    default void install() {
        STDIO.openInfo("Installing ", this.displayName());

        if (this.isInstalled()) {
            STDIO.closeWarn("skipped (already installed)");
            return;
        }

        try {
            String name = this.displayName() + "-" + this.source().version() + ".jar";
            Installable.download(this.source().link(), this.downloads(), name);
            this.installations().add(name);
            STDIO.closeInfoDone();
        } catch (IOException e) {
            STDIO.closeErr(e, "failed");
        }
    }

    default void update() {
        STDIO.openInfo("Updating ", this.displayName());
        String name = this.displayName() + "-" + this.source().version() + ".jar";
        File file;

        // Stash download
        try {
            file = Installable.stash(this.source().link());
        } catch (IOException e) {
            STDIO.closeErr( e, "failed (failed to download)");
            return;
        }

        // Uninstall
        for (String inst : this.installations()) {
            STDIO.debug("Uninstalling ", inst);
            File f = new File(this.downloads(), inst);
            if (!Files.isSymbolicLink(f.toPath())) {
                if (f.delete()) continue;
                STDIO.closeErr("failed (failed to delete ", f, ")");
            } else STDIO.closeWarn("skipped (already linked)");
            return;
        }
        this.installations().clear();

        // Install stashed
        try {
            Installable.finalise(file, this.downloads(), name);
            this.installations().add(name);
            STDIO.closeInfoDone();
        } catch (IOException e) {
            STDIO.closeErr(e, "failed (could not replace ", name, ")");
        }
    }

    default void uninstall() {
        for (String inst : this.installations()) {
            STDIO.openInfo("Uninstalling ", inst);
            File file = new File(this.downloads(), inst);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) {
                    this.installations().remove(inst);
                    STDIO.closeInfoDone();
                } else STDIO.closeErr("failed");
            } else STDIO.closeErr("skipped (linked)");
        }
    }

    //= Utils == == == == == == == == == == == ==

    /**
     * Download a file from the internet
     * @param uri The uri to download from
     * @param folder The target folder
     * @param name The target file name
     * @throws IOException If the download failed
     */
    private static void download(@NotNull URI uri, @NotNull File folder, @NotNull String name) throws IOException {
        File file = Installable.stash(uri);
        finalise(file, folder, name);
    }

    private static void finalise(@NotNull File stash, @NotNull File folder, @NotNull String name) throws IOException {
        folder.mkdirs();
        Files.copy(stash.toPath(), new File(folder, name).toPath());
        stash.delete();
    }

    private static @NotNull File stash(@NotNull URI uri) throws IOException {
        Setup.TEMP.mkdirs();
        File file;

        do {
            file = new File(Setup.TEMP, UUID.randomUUID().toString());
        } while (file.exists());

        ReadableByteChannel byteChannel = Channels.newChannel(uri.toURL().openStream());
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);

        return file;
    }
}
