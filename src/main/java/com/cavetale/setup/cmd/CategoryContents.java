package com.cavetale.setup.cmd;

import com.cavetale.setup.data.PluginCategory;
import link.l_pf.cmdlib.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Used for storing categories in commands and flags. */
public final class CategoryContents extends SetContents<PluginCategory> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws PluginCategory.NotFoundException {
        PluginCategory category = PluginCategory.get(arg);
        if (this.contents.contains(category)) STDIO.warn("Ignoring duplicate category \"", arg);
        else this.contents.add(category);
        return true;
    }
}
