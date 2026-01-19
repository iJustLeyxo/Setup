package com.cavetale.setup;

import com.cavetale.setup.cmd.CustomCommand;
import com.cavetale.setup.cmd.CustomFlag;
import com.cavetale.setup.data.Plugin;
import com.cavetale.setup.data.PluginCategory;
import com.cavetale.setup.data.ServerPreset;
import com.cavetale.setup.data.ServerSoftware;
import link.l_pf.cmdlib.app.App;
import link.l_pf.cmdlib.app.event.type.LoopStartEvent;
import org.jetbrains.annotations.NotNull;

/** Main class. */
public final class Setup {
    /** Runs the command line app. */
    static void main(String[] args) {
        App app = new App(CustomCommand.values(), CustomFlag.values());
        app.listeners().add(LoopStartEvent.class, Setup::reset);
        app.run(args);
    }

    /** Resets the state of the tool before every command. */
    private static void reset(@NotNull LoopStartEvent e) {
        Plugin.resetAll();
        PluginCategory.resetAll();
        ServerPreset.resetAll();
        ServerSoftware.resetAll();
    }
}
