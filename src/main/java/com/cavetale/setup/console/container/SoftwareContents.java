package com.cavetale.setup.console.container;

import com.cavetale.setup.data.server.Software;
import org.jetbrains.annotations.NotNull;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

/**
 * Software container, used to store software from a software flag
 */
public final class SoftwareContents extends SetContents<Software> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Software.NotFoundException {
        Software software = Software.get(arg);
        if (this.contents.contains(software)) SYSIO.warn("Ignoring duplicate software \"" + arg + "\n");
        else this.contents.add(software);
        return true;
    }
}
