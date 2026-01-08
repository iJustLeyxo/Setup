package com.cavetale.setup.cmd;

import com.cavetale.setup.data.ServerSoftware;
import link.l_pf.cmdlib.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Used for storing server software in commands and flags. */
public final class SoftwareContents extends SetContents<ServerSoftware> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws ServerSoftware.NotFoundException {
        ServerSoftware software = ServerSoftware.get(arg);
        if (this.contents.contains(software)) STDIO.warn("Ignoring duplicate software \"", arg);
        else this.contents.add(software);
        return true;
    }
}
