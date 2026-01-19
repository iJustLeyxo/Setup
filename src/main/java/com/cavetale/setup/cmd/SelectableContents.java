package com.cavetale.setup.cmd;

import com.cavetale.setup.data.Entry;
import link.l_pf.cmdlib.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.cavetale.setup.data.Entry.SelectType.USER;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Command and flag arguments container for the command line app. */
public final class SelectableContents<S extends Entry> extends SetContents<S> {
    /** Function for resolving input args. */
    private final @NotNull Function<String, S> find;

    public SelectableContents(@NotNull Function<String, S> find) {
        this.find = find;
    }

    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Entry.UnknownException {
        S sel = find.apply(arg);
        if (!contents.add(sel)) STDIO.warn("Ignoring duplicate reference ", arg);
        sel.selectAs(USER);
        return true;
    }
}
