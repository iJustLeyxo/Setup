package com.cavetale.setup.data;

import com.cavetale.setup.Setup;
import com.cavetale.setup.data.plugin.Plugin;
import com.cavetale.setup.download.Source;
import io.github.ijustleyxo.jclix.io.Style;
import io.github.ijustleyxo.jclix.io.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

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
            SYSIO.send(Type.INFO, Type.WARN, "Installing " + this.displayName(), " skipped (already installed)");
            return;
        }

        try {
            String name = this.displayName() + "-" + this.source().ver() + ".jar";
            Installable.download(this.source().uri(), this.downloads(), name);
            this.installations().add(name);
            SYSIO.info(Style.SUCCESS + " done\n");
        } catch (IOException e) {
            SYSIO.err(Style.INFO + "Installing " + this.displayName() + Style.ERR + " failed", e);
        }
    }

    default void update() {
        SYSIO.info("Updating " + this.displayName());

        String name = this.displayName() + "-" + this.source().ver() + ".jar";
        File file;

        // Stash download
        try {
            file = Installable.stash(this.source().uri());
        } catch (IOException e) {
            SYSIO.send(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to download", e);
            return;
        }

        // Uninstall
        for (String inst : this.installations()) {
            SYSIO.debug("Uninstalling " + inst);
            File f = new File(this.downloads(), inst);
            if (!Files.isSymbolicLink(f.toPath())) {
                if (f.delete()) continue;
                SYSIO.send(Type.INFO, Type.ERR, "Updating " + this.displayName(), " failed - failed to delete " + f + "\n");
            } else if (!SYSIO.info(Style.ERR + " failed - skipped " + f + " (linked)\n")) {
                SYSIO.err("Updating " + this.displayName() + " failed - skipped " + f + " (linked)\n");
            }
            return;
        }
        this.installations().clear();

        // Install stashed
        try {
            Installable.finalise(file, this.downloads(), name);
            this.installations().add(name);
            SYSIO.info(Style.SUCCESS + " done\n");
        } catch (IOException e) {
            SYSIO.err(Style.INFO + "Updating " + this.displayName() + Style.ERR + " failed - failed to download", e);
        }
    }

    default void uninstall() {
        for (String inst : this.installations()) {
            SYSIO.info("Uninstalling " + inst);
            File file = new File(this.downloads(), inst);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) {
                    this.installations().remove(inst);
                    SYSIO.info(Style.SUCCESS + " done\n");
                } else SYSIO.send(Type.INFO, Type.ERR, "Uninstalling " + file, " failed\n");
            } else SYSIO.send(Type.INFO, Type.WARN, "Uninstalling " + file, " skipped (linked)\n");
        }
    }

    //= Utils == == == == == == == == == == == ==

    static @NotNull URI uriOf(@NotNull String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new Plugin.URIError(uri, e);
        }
    }

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
