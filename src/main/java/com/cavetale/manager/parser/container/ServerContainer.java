package com.cavetale.manager.parser.container;

import com.cavetale.manager.data.plugin.Server;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Server container, used to store servers from a server flag
 */
public final class ServerContainer extends SetContainer<Server> {
    @Override
    public boolean add(@NotNull String arg) throws InputException {
        Server server = Server.get(arg);
        if (this.contents.contains(server)) Console.log(Type.INFO, "Ignoring duplicate \"" + arg + "\n");
        else this.contents.add(server);
        return true;
    }
}
