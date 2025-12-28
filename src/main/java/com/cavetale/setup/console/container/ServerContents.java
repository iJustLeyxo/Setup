package com.cavetale.setup.console.container;

import com.cavetale.setup.data.plugin.Server;
import link.l_pf.cmdlib.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Used for storing server configurations in commands and flags. */
public final class ServerContents extends SetContents<Server> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Server.NotFoundException {
        Server server = Server.get(arg);
        if (this.contents.contains(server)) STDIO.warn("Ignoring duplicate \"", arg);
        else this.contents.add(server);
        return true;
    }
}
