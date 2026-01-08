package com.cavetale.setup.cmd;

import com.cavetale.setup.data.PluginServer;
import link.l_pf.cmdlib.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Used for storing server configurations in commands and flags. */
public final class ServerContents extends SetContents<PluginServer> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws PluginServer.NotFoundException {
        PluginServer server = PluginServer.get(arg);
        if (this.contents.contains(server)) STDIO.warn("Ignoring duplicate \"", arg);
        else this.contents.add(server);
        return true;
    }
}
