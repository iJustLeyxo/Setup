package com.cavetale.setup.console.container;

import com.cavetale.setup.data.server.Software;
import io.github.ijustleyxo.jclix.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

/** Used for storing server software in commands and flags. */
public final class SoftwareContents extends SetContents<Software> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Software.NotFoundException {
        Software software = Software.get(arg);
        if (this.contents.contains(software)) SYSIO.warn("Ignoring duplicate software \"" + arg + "\n");
        else this.contents.add(software);
        return true;
    }
}
