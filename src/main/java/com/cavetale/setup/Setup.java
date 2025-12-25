package com.cavetale.setup;

import com.cavetale.setup.console.CustomCommand;
import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.data.plugin.Category;
import com.cavetale.setup.data.plugin.Plugin;
import com.cavetale.setup.data.plugin.Server;
import com.cavetale.setup.data.server.Software;
import link.l_pf.cmdlib.app.App;
import link.l_pf.cmdlib.app.event.type.PreCmdEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/** Cavetale installation manager, used to manage plugins and server software for testing server. */
public final class Setup {
    /** Temporary file location. */
    public static final @NotNull File TEMP = new File(".setup/");

    /**
     * Runs the setup tool.
     * @param args The arguments to run with.
     */
    static void main(String[] args) {
        App app = new App(CustomCommand.values(), CustomFlag.values());
        app.listeners().add(PreCmdEvent.class, Setup::reset);
        app.run(args);
    }

    /**
     * Resets the setup tool state.
     * @param e The event to reset on.
     */
    private static void reset(@NotNull PreCmdEvent e) {
        Category.reset();
        Plugin.reset();
        Server.reset();
        Software.reset();
    }
}
