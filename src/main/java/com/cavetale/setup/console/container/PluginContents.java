package com.cavetale.setup.console.container;

import com.cavetale.setup.data.plugin.Plugin;
import io.github.ijustleyxo.jclix.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

/** Used for storing plugins in commands and flags. */
public final class PluginContents extends SetContents<Plugin> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Plugin.NotFoundException {
        Plugin plugin = Plugin.get(arg);
        if (this.contents.contains(plugin)) SYSIO.warn("Ignoring duplicate plugins \"" + arg + "\n");
        else this.contents.add(plugin);
        return true;
    }
}
