package com.cavetale.setup.console.container;

import com.cavetale.setup.data.plugin.Server;
import org.jetbrains.annotations.NotNull;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

/**
 * Server container, used to store servers from a server flag
 */
public final class ServerContents extends SetContents<Server> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Server.NotFoundException {
        Server server = Server.get(arg);
        if (this.contents.contains(server)) SYSIO.warn("Ignoring duplicate \"" + arg + "\n");
        else this.contents.add(server);
        return true;
    }
}
