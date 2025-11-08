package com.cavetale.setup;

import com.cavetale.setup.console.CustomCommand;
import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.data.plugin.Category;
import com.cavetale.setup.data.plugin.Plugin;
import com.cavetale.setup.data.plugin.Server;
import com.cavetale.setup.data.server.Software;
import io.github.ijustleyxo.jclix.app.App;
import io.github.ijustleyxo.jclix.app.event.type.PreCmdEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Cavetale installation manager, used to manage plugins and server software for testing servers
 */
public final class Setup {
    public static final @NotNull File TEMP = new File(".setup/");

    public static void main(String[] args) {
        App app = new App(CustomCommand.values(), CustomFlag.values());
        app.listeners().add(PreCmdEvent.class, Setup::reset);
        app.run(args);
    }

    private static void reset(@NotNull PreCmdEvent e) {
        Category.reset();
        Plugin.reset();
        Server.reset();
        Software.reset();
    }

    // TODO: Resource pack download (with hash)
}