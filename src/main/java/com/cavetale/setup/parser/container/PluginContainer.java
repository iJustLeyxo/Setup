package com.cavetale.setup.parser.container;

import com.cavetale.setup.data.plugin.Plugin;
import com.cavetale.setup.parser.InputException;
import com.cavetale.setup.util.console.Console;
import com.cavetale.setup.util.console.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin container, used to store plugins from a plugin flag
 */
public final class PluginContainer extends SetContainer<Plugin> {
    @Override
    public boolean add(@NotNull String arg) throws InputException {
        Plugin plugin = Plugin.get(arg);
        if (this.contents.contains(plugin)) Console.log(Type.INFO, "Ignoring duplicate plugins \"" + arg + "\n");
        else this.contents.add(plugin);
        return true;
    }
}
